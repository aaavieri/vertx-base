package com.yjl.vertx.base.com.factory.family;

import com.google.inject.*;
import com.yjl.vertx.base.com.anno.initializer.ComponentInitializer;
import com.yjl.vertx.base.com.anno.initializer.FirstClassComponent;
import com.yjl.vertx.base.com.anno.initializer.OverrideDependency;
import com.yjl.vertx.base.com.exception.FrameworkException;
import com.yjl.vertx.base.com.factory.family.listener.AddDependencyListener;
import com.yjl.vertx.base.com.factory.family.listener.NodeStatusListener;
import com.yjl.vertx.base.com.util.ReflectionsUtil;
import com.yjl.vertx.base.com.verticle.InitVerticle;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FactoryFamily {

	private InitVerticle initVerticle;

	@Getter
	private FactoryFamilyNode rootNode;

	@Getter
	private Injector injector;

	private Map<FactoryFamilyNode, Set<FactoryFamilyNode>> dependencyChainMap = new HashMap<>();

	private Map<FactoryFamilyNode, Set<FactoryFamilyNode>> reverseDependencyChainMap = new HashMap<>();

	private Set<FactoryFamilyNode> allNodes = new HashSet<>();

	private Set<FactoryFamilyNode> firstClassNodes = new HashSet<>();

	private List<AddDependencyListener> addDependencyListeners = new ArrayList<>();

	private List<NodeStatusListener> nodeStatusListeners = new ArrayList<>();

	private Set<OverrideDependency> overrideDependencies = new HashSet<>();

	public FactoryFamily initFamilyTree(InitVerticle verticle) {
		this.initVerticle = verticle;
		this.rootNode = new FactoryFamilyNode().realNode(new VirtualRootNode(verticle.getRootFactory().getClass()))
			.family(this).nodeStatusListeners(this.nodeStatusListeners).nodeInstance(verticle.getRootFactory()).layer(0);
		return this;
	}

	public FactoryFamily listenAddDependency(AddDependencyListener listener) {
		this.addDependencyListeners.add(listener);
		return this;
	}

	public FactoryFamily listenNodeStatus(NodeStatusListener listener) {
		this.nodeStatusListeners.add(listener);
		return this;
	}

	public FactoryFamily addDependencyGroup(FactoryFamilyNode node, FactoryFamilyNode dependency) {
		if (!this.dependencyChainMap.containsKey(node)) {
			this.dependencyChainMap.put(node, new HashSet<>());
		}
		this.dependencyChainMap.get(node).add(dependency.layer(node.layer() + 1));
		if (!this.reverseDependencyChainMap.containsKey(dependency)) {
			this.reverseDependencyChainMap.put(dependency, new HashSet<>());
		}
		this.reverseDependencyChainMap.get(dependency).add(node);
		this.addDependencyListeners.forEach(listener -> listener.onAdd(node, dependency));
		return this;
	}

	public FactoryFamily initFamily() {
		this.allNodes.add(this.rootNode);
		this.addFirstClassNode(this.rootNode, this.initVerticle.getClass());
		this.addNode(this.rootNode, this.initVerticle.getClass());
		this.addRestOverrideDependency();
		return this;
	}

	public FactoryFamily start() {
		this.rootNode.beforeStart();
		this.injector = Guice.createInjector(this.rootNode.nodeInstance());
		this.rootNode.injector(this.injector).afterStart();
		if (this.firstClassNodes.size() > 0) {
			this.injector = this.batchStartNodes(this.firstClassNodes, this.injector);
		}
		int startLayer = this.allNodes.stream().mapToInt(FactoryFamilyNode::layer).max().orElse(1);
		int waitingCount = 0;
		do {
			int nowLayer = startLayer;
			Set<FactoryFamilyNode> layerNodes = this.allNodes.stream().filter(node -> node.layer() == nowLayer).collect(Collectors.toSet());
			Set<FactoryFamilyNode> readyNodes = Stream.concat(layerNodes.stream().filter(this::isReady),
				this.allNodes.stream().filter(node -> node.layer() > nowLayer).filter(this::isReady)).collect(Collectors.toSet());
			waitingCount = this.getWaitingCount();
			if (readyNodes.size() == 0 && waitingCount > 0) {
				throw new FrameworkException().message("factory start in dead loops, please check your @ComponentInitializer annotation");
			}
			this.injector = this.batchStartNodes(readyNodes, this.injector);
			startLayer--;
		} while (startLayer > 0);
		this.allNodes.parallelStream().filter(node -> node.status() == FactoryFamilyNode.STATUS_COMPLETE_CONFIGURE)
			.forEach(node -> node.nodeInstance().afterConfigure());
		return this;
	}

	public FactoryFamily stop() {
		return this;
	}

	private boolean isReady(FactoryFamilyNode node) {
		return node.status() == FactoryFamilyNode.STATUS_WAITING_CONFIGURE
			&& (!this.dependencyChainMap.containsKey(node) || this.dependencyChainMap.get(node).stream()
			.allMatch(dependency -> dependency.status() == FactoryFamilyNode.STATUS_COMPLETE_CONFIGURE));
	}

	private Injector batchStartNodes(Set<FactoryFamilyNode> nodes, Injector parentInjector) {
		Injector initModuleInjector = parentInjector.createChildInjector(new AbstractModule() {
			@Override
			protected void configure() {
				nodes.stream().map(readyNode -> new PrivateModule() {
					@Override
					protected void configure() {
						this.bind(readyNode.realNode().factoryClass()).asEagerSingleton();
						this.bind(ComponentInitializer.class).toInstance(readyNode.realNode());
						this.expose(readyNode.realNode().factoryClass());
					}
				}).forEach(this::install);
			}
		});
		nodes.forEach(node -> node.nodeInstance(initModuleInjector.getInstance(node.realNode().factoryClass())).beforeStart());
		Injector configureModuleInjector = initModuleInjector.createChildInjector(
			nodes.stream().map(FactoryFamilyNode::nodeInstance).collect(Collectors.toList()));
		nodes.forEach(FactoryFamilyNode::startCompleted);
		return configureModuleInjector;
	}

	private int getWaitingCount() {
		return ReflectionsUtil.<Long>autoCast(this.allNodes.stream()
			.filter(node -> node.status() == FactoryFamilyNode.STATUS_WAITING_CONFIGURE).count()).intValue();
	}

//	private Set<FactoryFamilyNode> getReadyNodes() {
//		Set<FactoryFamilyNode> dependentNodes = this.allNodes.stream().filter(realNode ->
//			realNode.status() == FactoryFamilyNode.STATUS_WAITING_CONFIGURE && !this.dependencyChainMap.containsKey(realNode)
//			&& !this.dependencyChainMap.get(this.rootNode).contains(realNode)).collect(Collectors.toSet());
//		if (dependentNodes.size() > 0) {
//			return dependentNodes;
//		} else {
//			return this.allNodes.stream().filter(realNode ->
//				realNode.status() == FactoryFamilyNode.STATUS_WAITING_CONFIGURE && (!this.dependencyChainMap.containsKey(realNode)
//					|| this.dependencyChainMap.get(realNode).stream()
//					.allMatch(dependency -> dependency.status() == FactoryFamilyNode.STATUS_COMPLETE_CONFIGURE)))
//				.collect(Collectors.toSet());
//		}
//	}

	private void addFirstClassNode(FactoryFamilyNode familyNode, Class<?> defineClass) {
		Stream.of(defineClass.getAnnotationsByType(FirstClassComponent.class)).flatMap(firstClassComponent -> Stream.of(firstClassComponent.value()))
			.map(this::addToAllAndFetch)
			.peek(dependencyNode -> this.addDependencyGroup(familyNode, dependencyNode))
			.forEach(this.firstClassNodes::add);
	}

	private void addNode(FactoryFamilyNode familyNode) {
		this.addNode(familyNode, familyNode.realNode().factoryClass());
	}

	private void addNode(FactoryFamilyNode familyNode, Class<?> defineClass) {
		this.overrideDependencies.addAll(Arrays.asList(defineClass.getAnnotationsByType(OverrideDependency.class)));
		Set<ComponentInitializer> dependencies = Stream.of(defineClass.getAnnotationsByType(ComponentInitializer.class))
			.collect(Collectors.toSet());
		this.overrideDependencies.stream().filter(overrideDependency ->
			overrideDependency.value().factoryClass().equals(familyNode.realNode().factoryClass())).findFirst()
			.ifPresent(overrideDependency -> {
				familyNode.realNode(overrideDependency.value());
				if (overrideDependency.dependNothing()) {
					dependencies.clear();
					return;
				}
				if (overrideDependency.customAll().length > 0) {
					dependencies.clear();
					dependencies.addAll(Arrays.asList(overrideDependency.customAll()));
				} else {
					dependencies.removeIf(dependency -> Arrays.binarySearch(overrideDependency.customExclude(), dependency.factoryClass()) >= 0);
					dependencies.addAll(Arrays.asList(overrideDependency.customInclude()));
				}
			});
		dependencies.stream().map(this::addToAllAndFetch)
			.peek(dependencyNode -> this.addDependencyGroup(familyNode, dependencyNode))
			.forEach(this::addNode);
	}

	private void addRestOverrideDependency() {
		this.overrideDependencies.stream().filter(overrideDependency ->
			this.allNodes.stream().noneMatch(node -> node.realNode().factoryClass().equals(overrideDependency.value().factoryClass())))
			.forEach(overrideDependency -> {
				FactoryFamilyNode parentNode = new FactoryFamilyNode().realNode(overrideDependency.value()).nodeStatusListeners(this.nodeStatusListeners);
				this.allNodes.add(parentNode);
				this.addDependencyGroup(this.rootNode, parentNode);
				Stream.concat(Stream.of(overrideDependency.customInclude()), Stream.of(overrideDependency.customAll()))
					.filter(initializer -> Arrays.binarySearch(overrideDependency.customExclude(), initializer.factoryClass()) < 0)
					.map(this::addToAllAndFetch)
					.peek(dependency -> this.addDependencyGroup(parentNode, dependency))
					.forEach(this::addNode);
			});
	}

	private FactoryFamilyNode addToAllAndFetch(ComponentInitializer initializer) {
		return this.allNodes.stream().filter(node -> node.realNode().equals(initializer)).findFirst()
			.orElseGet(() -> {
				FactoryFamilyNode newNode = new FactoryFamilyNode().nodeStatusListeners(this.nodeStatusListeners).realNode(initializer);
				this.allNodes.add(newNode);
				return newNode;
			});
	}
}
