package com.yjl.vertx.base.com.factory.command;

import com.yjl.vertx.base.com.command.RunProfileCommand;
import io.vertx.core.spi.launcher.DefaultCommandFactory;

public class RunProfileCommandFactory extends DefaultCommandFactory<RunProfileCommand> {
    public RunProfileCommandFactory() {
        super(RunProfileCommand.class);
    }
}
