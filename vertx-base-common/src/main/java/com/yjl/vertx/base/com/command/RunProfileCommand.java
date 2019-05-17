package com.yjl.vertx.base.com.command;

import io.vertx.core.cli.annotations.Description;
import io.vertx.core.cli.annotations.Name;
import io.vertx.core.cli.annotations.Option;
import io.vertx.core.cli.annotations.Summary;
import io.vertx.core.impl.launcher.commands.RunCommand;

import java.io.InputStream;

@Name("runProfile")
@Summary("Runs a verticle called <main-verticle> in its own instance of vert.x. And set config by passed profile")
public final class RunProfileCommand extends RunCommand {
    
    private final static String SRC_CONF_PATH = "src/main/resources/config.%profile%.json";
    
    private final static String JAR_CONF_PATH = "config.%profile%.json";
    
    public RunProfileCommand() {
        super();
    }
    
    private String profile = "dev";
    
    private boolean srcMode = true;
    
    private boolean srcModeSet = false;
    
    @Option(longName = "profile")
    @Description("Specified a profile, and  app will use it to bind the config.<profile>.json.")
    public void setProfile(String profile) {
        this.profile = profile;
        if (!this.srcModeSet) {
            this.srcMode = !"prod".equalsIgnoreCase(profile);
        }
    }
    
    @Option(longName = "srcMode")
    @Description("Is run in source or jar")
    public void setSrcMode(boolean srcMode) {
        this.srcMode = srcMode;
        this.srcModeSet = true;
    }
    
    public void run() {
        String conf = this.srcMode ? this.getSrcConf() : this.getJarConf();
        super.setConfig(conf);
        super.run();
    }
    
    private String getSrcConf() {
        return SRC_CONF_PATH.replace("%profile%", this.profile);
    }
    
    private String getJarConf() {
        InputStream inputStream = null;
        try {
            inputStream = this.getClass().getClassLoader().getResource(JAR_CONF_PATH.replace("%profile%", this.profile)).openStream();
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            return new String(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                }
            }
        }
        return null;
    }
}
