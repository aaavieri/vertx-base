package com.yjl.vertx.base.com.command;

import io.vertx.core.cli.annotations.Description;
import io.vertx.core.cli.annotations.Name;
import io.vertx.core.cli.annotations.Option;
import io.vertx.core.cli.annotations.Summary;
import io.vertx.core.impl.launcher.commands.RunCommand;

@Name("runProfile")
@Summary("Runs a verticle called <main-verticle> in its own instance of vert.x. And set config by passed profile")
public final class RunProfileCommand extends RunCommand {

  private final static String CONF_PATH = "src/main/resources/config.%profile%.json";

  public RunProfileCommand() {
    super();
  }

  private String profile = "dev";

  @Option(longName = "profile")
  @Description("Specified a profile, and app will use it to bind the config.<profile>.json.")
  public void setProfile(String profile) {
    this.profile = profile;
  }

  public void run() {
    super.setConfig(CONF_PATH.replace("%profile%", this.profile));
    super.run();
  }
}
