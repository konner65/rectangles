package org.konner.rectangles.shell;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.shell.core.command.AbstractCommand;
import org.springframework.shell.core.command.Command;
import org.springframework.shell.core.command.CommandArgument;
import org.springframework.shell.core.command.CommandContext;
import org.springframework.shell.core.command.CommandRegistry;
import org.springframework.shell.core.command.ExitStatus;
import org.springframework.shell.core.command.ParsedInput;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RectangleHelpCommandTest {

    private CommandRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new CommandRegistry();
        registry.registerCommand(fakeCommand("analyze", "Rectangle Analysis Commands", "Analyze two rectangles."));
        registry.registerCommand(fakeCommand("demo", "Rectangle Analysis Commands", "Run a built-in demo scenario."));
        registry.registerCommand(fakeCommand("clear", "Built-In Commands", "Clear the terminal screen."));
        registry.registerCommand(fakeCommand("script", "Built-In Commands", "Execute a script file."));
        registry.registerCommand(fakeCommand("version", "Built-In Commands", "Show version info."));
    }

    @Test
    void nameIsHelpSoTheRegistryCanDispatchToIt() {
        assertThat(helpCommand(false).getName()).isEqualTo("help");
    }

    @Test
    void keepsTheGroupInheritedFromTheSuperclass() {
        assertThat(helpCommand(false).getGroup()).isEqualTo("Built-In Commands");
    }

    @Nested
    class InteractiveListMode {

        @Test
        void rectangleAnalysisCommandsGroupAppearsBeforeBuiltInCommands() throws Exception {
            String output = runHelp(/* interactive = */ true, /* args = */ List.of());

            int rectangleGroupIdx = output.indexOf("Rectangle Analysis Commands");
            int builtInGroupIdx = output.indexOf("Built-In Commands");
            assertThat(rectangleGroupIdx)
                    .as("Rectangle Analysis Commands must be present")
                    .isNotNegative();
            assertThat(builtInGroupIdx)
                    .as("Built-In Commands must be present")
                    .isNotNegative();
            assertThat(rectangleGroupIdx).isLessThan(builtInGroupIdx);
        }

        @Test
        void commandsAreSortedAlphabeticallyWithinEachGroup() throws Exception {
            String output = runHelp(true, List.of());
            assertThat(output.indexOf("\tanalyze:")).isLessThan(output.indexOf("\tdemo:"));
            assertThat(output.indexOf("\tclear:"))
                    .isLessThan(output.indexOf("\tscript:"))
                    .isLessThan(output.indexOf("\tversion:"));
        }

        @Test
        void includesTheSyntheticQuitCommandWithItsExitAlias() throws Exception {
            String output = runHelp(true, List.of());
            assertThat(output).contains("quit, exit: Exit the shell");
        }

        @Test
        void blankCommandArgumentIsTreatedAsListMode() throws Exception {
            String output = runHelp(true, List.of(argument("   ")));
            assertThat(output).contains("AVAILABLE COMMANDS")
                    .contains("Rectangle Analysis Commands")
                    .contains("Built-In Commands");
        }
    }

    @Nested
    class OneShotListMode {

        @Test
        void stillShowsTheBuiltInCommandsGroupHeading() throws Exception {
            String output = runHelp(/* interactive = */ false, List.of());
            assertThat(output).contains("Built-In Commands");
        }

        @Test
        void keepsOnlyHelpAndVersionFromTheBuiltInsGroup() throws Exception {
            String output = runHelp(false, List.of());
            assertThat(output)
                    .contains("\thelp:")
                    .contains("\tversion:");
        }

        @Test
        void hidesTheBuiltInsThatOnlyMakeSenseInAReplSession() throws Exception {
            String output = runHelp(false, List.of());
            assertThat(output)
                    .doesNotContain("\tclear:")
                    .doesNotContain("\tscript:")
                    .doesNotContain("quit, exit:");
        }

        @Test
        void stillListsRectangleAnalysisCommands() throws Exception {
            String output = runHelp(false, List.of());
            assertThat(output)
                    .contains("AVAILABLE COMMANDS")
                    .contains("Rectangle Analysis Commands")
                    .contains("\tanalyze:")
                    .contains("\tdemo:");
        }

        @Test
        void rectangleAnalysisCommandsStillAppearsBeforeBuiltInCommands() throws Exception {
            String output = runHelp(false, List.of());
            assertThat(output.indexOf("Rectangle Analysis Commands"))
                    .isLessThan(output.indexOf("Built-In Commands"));
        }
    }

    @Nested
    class PerCommandMode {

        @Test
        void delegatesToTheSuperclassWhichRendersTheCommandSpecificNameBlock() throws Exception {
            String output = runHelp(false, List.of(argument("analyze")));
            assertThat(output)
                    .contains("NAME")
                    .contains("analyze - Analyze two rectangles.");
        }

        @Test
        void unknownCommandFallsBackToTheListHelpFromTheSuperclass() throws Exception {
            String output = runHelp(true, List.of(argument("does-not-exist")));
            // super.execute() falls back to Utils.formatAvailableCommands which
            // still uses alphabetical ordering, so we only assert that both
            // groups are present and the known commands show up.
            assertThat(output).contains("AVAILABLE COMMANDS")
                    .contains("Rectangle Analysis Commands")
                    .contains("Built-In Commands");
        }
    }

    private RectangleHelpCommand helpCommand(boolean interactiveEnabled) {
        RectangleHelpCommand cmd = new RectangleHelpCommand(interactiveEnabled);
        // Register with the shared registry so super.execute() can resolve
        // command names for per-command help.
        registry.registerCommand(cmd);
        return cmd;
    }

    private String runHelp(boolean interactiveEnabled, List<CommandArgument> arguments) throws Exception {
        RectangleHelpCommand cmd = helpCommand(interactiveEnabled);
        StringWriter buffer = new StringWriter();
        PrintWriter writer = new PrintWriter(buffer);
        ParsedInput parsedInput = new ParsedInput("help", List.of(), List.of(), arguments);
        CommandContext ctx = new CommandContext(parsedInput, registry, writer, null);
        ExitStatus status = cmd.execute(ctx);
        writer.flush();
        assertThat(status.code()).isEqualTo(ExitStatus.OK.code());
        return buffer.toString();
    }

    private static CommandArgument argument(String value) {
        return new CommandArgument(0, value);
    }

    private static Command fakeCommand(String name, String group, String description) {
        return new AbstractCommand(name, description, group) {
            @Override
            public ExitStatus doExecute(CommandContext commandContext) {
                return ExitStatus.OK;
            }
        };
    }
}
