package com.yjl.vertx.base.com.factory.family;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.PrivateModule;
import com.yjl.vertx.base.com.anno.initializer.ComponentInitializer;
import com.yjl.vertx.base.com.anno.initializer.FirstClassComponent;
import com.yjl.vertx.base.com.anno.initializer.OverrideDependency;
import com.yjl.vertx.base.com.enumeration.FactoryDefinition;
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

//	private Set<FactoryFamilyNode> firstClassNodes = new HashSet<>();

	private List<AddDependencyListener> addDependencyListeners = new ArrayList<>();

	private List<NodeStatusListener> nodeStatusListeners = new ArrayList<>();

	private Set<OverrideDependency> overrideDependencies = new HashSet<>();

	public FactoryFamily initFamilyTree(InitVerticle verticle) {
		this.initVerticle = verticle;
		this.rootNode = new FactoryFamilyNode().realNode(new VirtualRootNode(verticle.getRootFactory().getClass()))
			.family(this).nodeStatusListeners(this.nodeStatusListeners).nodeInstance(verticle.getRootFactory()).layer(0);
//        this.addFirstClassNode(this.rootNode, this.initVerticle.getClass());
        this.addDefineNode(this.rootNode, verticle.getClass());
		return this;
	}

    private void addDefineNode(FactoryFamilyNode parentNode, Class<?> defineClass) {
        List<FactoryFamilyNode> dependencyNodes = Stream.of(this.getFirstClassNode(parentNode, defineClass),
            this.getInitializerNode(parentNode, defineClass), this.getOverrideNode(parentNode, defineClass))
            .flatMap(Collection::stream).collect(Collectors.toList());
        List<FactoryFamilyNode> reGetDependencyNodes = this.reGetOverrideDependencies(parentNode, dependencyNodes, defineClass);
        reGetDependencyNodes.forEach(dependencyNode -> {
            Optional<FactoryFamilyNode> findNode = this.allNodes.stream().filter(node -> node.realNode().factoryClass()
                .equals(dependencyNode.realNode().factoryClass()))
                .findFirst();
            if (findNode.isPresent()) {
                FactoryFamilyNode mergedNode = this.mergeNode(dependencyNode, findNode.get());
                this.addDependencyGroup(parentNode, mergedNode);
            } else {
                this.allNodes.add(dependencyNode);
                this.addDependencyGroup(parentNode, dependencyNode);
                this.addDefineNode(dependencyNode, dependencyNode.realNode().factoryClass());
            }
        });
    }
    
    private FactoryFamilyNode mergeNode(FactoryFamilyNode src, FactoryFamilyNode dest) {
        FactoryFamilyNode node = Stream.of(src, dest).min(Comparator.comparingInt(compare -> compare.definition().getOrder())).get();
        dest.realNode(node.realNode()).layer(Math.max(src.layer(), dest.layer())).definition(node.definition())
            .family(node.family()).nodeStatusListeners(node.nodeStatusListeners());
        return dest;
    }
    
    private List<FactoryFamilyNode> reGetOverrideDependencies(FactoryFamilyNode parentNode, List<FactoryFamilyNode> dependencyNodes, Class<?> defineClass) {
        List<FactoryFamilyNode> overrideDependencies = new ArrayList<>(dependencyNodes);
        this.overrideDependencies.stream().filter(overrideDependency ->
            overrideDependency.value().factoryClass().equals(parentNode.realNode().factoryClass())).findFirst()
            .ifPresent(overrideDependency -> {
                Set<ComponentInitializer> dependencies = overrideDependencies.stream().map(FactoryFamilyNode::realNode)
                    .collect(Collectors.toSet());
                overrideDependencies.clear();
                if (overrideDependency.dependNothing()) {
                    dependencies.clear();
                    return;
                }
                if (overrideDependency.customAll().length > 0) {
                    dependencies.clear();
                    dependencies.addAll(Arrays.asList(overrideDependency.customAll()));
                } else {
                    dependencies.removeIf(dependency -> Stream.concat(Stream.of(overrideDependency.customExclude()),
                        Stream.of(overrideDependency.customInclude()).map(ComponentInitializer::factoryClass))
                        .anyMatch(factoryClass -> factoryClass.equals(dependency.factoryClass())));
                    dependencies.addAll(Arrays.asList(overrideDependency.customInclude()));
                }
                dependencies.forEach(dependency -> overrideDependencies.add(this.getNode(dependency, parentNode)
                    .definition(defineClass.equals(this.initVerticle.getClass())
                        ? FactoryDefinition.VERTICLE_OVERRIDE : FactoryDefinition.OTHER_FACTORY_OVERRIDE)));
            });
        return overrideDependencies;
    }
    
    private List<FactoryFamilyNode> getFirstClassNode(FactoryFamilyNode parentNode, Class<?> defineClass) {
        return Stream.of(defineClass.getAnnotationsByType(FirstClassComponent.class))
            .flatMap(firstClassComponent -> Stream.of(firstClassComponent.value()))
            .map(initializer -> this.getNode(initializer, parentNode)
                .definition(defineClass.equals(this.initVerticle.getClass())
                    ? FactoryDefinition.VERTICLE_FIRSTCLASS : FactoryDefinition.OTHER_FACTORY_FIRSTCLASS))
            .collect(Collectors.toList());
    }
    
    private List<FactoryFamilyNode> getInitializerNode(FactoryFamilyNode parentNode, Class<?> defineClass) {
        return Stream.of(defineClass.getAnnotationsByType(ComponentInitializer.class))
            .map(initializer -> this.getNode(initializer, parentNode)
                .definition(defineClass.equals(this.initVerticle.getClass())
                    ? FactoryDefinition.VERTICLE_INITIALIZER : FactoryDefinition.OTHER_FACTORY_INITIALIZER))
            .collect(Collectors.toList());
    }
    
    private List<FactoryFamilyNode> getOverrideNode(FactoryFamilyNode parentNode, Class<?> defineClass) {
        return Stream.of(defineClass.getAnnotationsByType(OverrideDependency.class))
            .peek(this.overrideDependencies::add)
            .map(override -> this.getNode(override.value(), parentNode)
                    .definition(defineClass.equals(this.initVerticle.getClass())
                        ? FactoryDefinition.VERTICLE_OVERRIDE : FactoryDefinition.OTHER_FACTORY_OVERRIDE))
            .collect(Collectors.toList());
    }
    
    private FactoryFamilyNode getNode(ComponentInitializer initializer, FactoryFamilyNode parent) {
	    return new FactoryFamilyNode().nodeStatusListeners(this.nodeStatusListeners)
            .realNode(initializer).family(this).layer(parent.layer() + 1);
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
//		this.dependencyChainMap.get(node).add(dependency.layer(node.layer() + 1));
        this.dependencyChainMap.get(node).add(dependency);
		if (!this.reverseDependencyChainMap.containsKey(dependency)) {
			this.reverseDependencyChainMap.put(dependency, new HashSet<>());
		}
		this.reverseDependencyChainMap.get(dependency).add(node);
		this.addDependencyListeners.forEach(listener -> listener.onAdd(node, dependency));
		return this;
	}

	// change another way to init family
//	public FactoryFamily initFamily() {
//		this.allNodes.add(this.rootNode);
//		this.addFirstClassNode(this.rootNode, this.initVerticle.getClass());
//		this.addNode(this.rootNode, this.initVerticle.getClass());
//		this.addRestOverrideDependency();
//		return this;
//	}

	public FactoryFamily start() {
		this.rootNode.beforeStart();
		this.injector = Guice.createInjector(this.rootNode.nodeInstance());
		this.rootNode.injector(this.injector).afterStart();
//		if (this.firstClassNodes.size() > 0) {
//			this.injector = this.batchStartNodes(this.firstClassNodes, this.injector);
//		}
		int startLayer = this.allNodes.stream().mapToInt(FactoryFamilyNode::layer).max().orElse(1);
		int waitingCount = 0;
		do {
		    Set<FactoryFamilyNode> readyFirstClassNodes = this.allNodes.stream().filter(node ->
                this.isReady(node) &&
                    (node.definition().equals(FactoryDefinition.VERTICLE_FIRSTCLASS)
                    || node.definition().equals(FactoryDefinition.OTHER_FACTORY_FIRSTCLASS))).collect(Collectors.toSet());
		    if (!readyFirstClassNodes.isEmpty()) {
                this.injector = this.batchStartNodes(readyFirstClassNodes, this.injector);
            }
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

//	private void addFirstClassNode(FactoryFamilyNode familyNode, Class<?> defineClass) {
//		Stream.of(defineClass.getAnnotationsByType(FirstClassComponent.class)).flatMap(firstClassComponent -> Stream.of(firstClassComponent.value()))
//			.map(this::addToAllAndFetch)
//			.peek(dependencyNode -> this.addDependencyGroup(familyNode, dependencyNode))
//			.forEach(this.firstClassNodes::add);
//	}

//	private void addNode(FactoryFamilyNode familyNode) {
//		this.addNode(familyNode, familyNode.realNode().factoryClass());
//	}
//
//	private void addNode(FactoryFamilyNode familyNode, Class<?> defineClass) {
//		this.overrideDependencies.addAll(Arrays.asList(defineClass.getAnnotationsByType(OverrideDependency.class)));
//		Set<ComponentInitializer> dependencies = Stream.of(defineClass.getAnnotationsByType(ComponentInitializer.class))
//			.collect(Collectors.toSet());
//		this.overrideDependencies.stream().filter(overrideDependency ->
//			overrideDependency.value().factoryClass().equals(familyNode.realNode().factoryClass())).findFirst()
//			.ifPresent(overrideDependency -> {
//				familyNode.realNode(overrideDependency.value());
//				if (overrideDependency.dependNothing()) {
//					dependencies.clear();
//					return;
//				}
//				if (overrideDependency.customAll().length > 0) {
//					dependencies.clear();
//					dependencies.addAll(Arrays.asList(overrideDependency.customAll()));
//				} else {
//                    dependencies.removeIf(dependency -> Stream.concat(Stream.of(overrideDependency.customExclude()),
//                        Stream.of(overrideDependency.customInclude()).map(ComponentInitializer::factoryClass))
//                        .anyMatch(factoryClass -> factoryClass.equals(dependency.factoryClass())));
//					dependencies.addAll(Arrays.asList(overrideDependency.customInclude()));
//				}
//			});
//		dependencies.stream().map(this::addToAllAndFetch)
//			.peek(dependencyNode -> this.addDependencyGroup(familyNode, dependencyNode))
//			.forEach(this::addNode);
//	}

//	private void addRestOverrideDependency() {
//		this.overrideDependencies.stream().filter(overrideDependency ->
//			this.allNodes.stream().noneMatch(node -> node.realNode().factoryClass().equals(overrideDependency.value().factoryClass())))
//			.forEach(overrideDependency -> {
//				FactoryFamilyNode parentNode = new FactoryFamilyNode().realNode(overrideDependency.value()).nodeStatusListeners(this.nodeStatusListeners);
//				this.allNodes.add(parentNode);
//				this.addDependencyGroup(this.rootNode, parentNode);
//				this.addNode(parentNode, overrideDependency.value().factoryClass());
//				Stream.concat(Stream.of(overrideDependency.customInclude()), Stream.of(overrideDependency.customAll()))
//					.filter(initializer -> Arrays.binarySearch(overrideDependency.customExclude(), initializer.factoryClass()) < 0)
//					.map(this::addToAllAndFetch)
//					.peek(dependency -> this.addDependencyGroup(parentNode, dependency))
//					.forEach(this::addNode);
//			});
//	}
//
//	private FactoryFamilyNode addToAllAndFetch(ComponentInitializer initializer) {
//		return this.allNodes.stream().filter(node -> node.realNode().equals(initializer)).findFirst()
//			.orElseGet(() -> {
//				FactoryFamilyNode newNode = new FactoryFamilyNode().nodeStatusListeners(this.nodeStatusListeners).realNode(initializer);
//				this.allNodes.add(newNode);
//				return newNode;
//			});
//	}
}
