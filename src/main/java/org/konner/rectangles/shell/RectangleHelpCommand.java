package org.konner.rectangles.shell;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.shell.core.command.Command;
import org.springframework.shell.core.command.CommandArgument;
import org.springframework.shell.core.command.CommandContext;
import org.springframework.shell.core.command.CommandRegistry;
import org.springframework.shell.core.command.ExitStatus;
import org.springframework.shell.core.command.Help;
import org.springframework.shell.core.utils.Utils;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Replaces Spring Shell's built-in {@link Help} so the {@code Rectangle
 * Analysis Commands} group is listed before {@code Built-In Commands} in the
 * available-commands list, and so the built-ins that don't apply in one-shot
 * mode are hidden.
 *
 * <p>Spring Shell's default renderer sorts groups purely by name, which would
 * put {@code Built-In Commands} first. This subclass keeps the superclass's
 * detailed per-command help rendering (so {@code help analyze} still produces
 * a nice NAME / SYNOPSIS / OPTIONS block) and only overrides the list-mode
 * output to sort with a preferred group order.
 *
 * <p>In one-shot mode most of the Spring Shell built-ins ({@code clear},
 * {@code quit}/{@code exit}, {@code script}) don't apply — users run a
 * single command and the process exits — so those entries are filtered out
 * when {@code spring.shell.interactive.enabled} is {@code false}. Only
 * {@code help} and {@code version} remain visible under the
 * {@code Built-In Commands} group.
 *
 * <p>The built-in bean is disabled via
 * {@code spring.shell.command.help.enabled=false}; this bean takes its place.
 */
@Component
public class RectangleHelpCommand extends Help {

    private static final String BUILT_IN_GROUP = "Built-In Commands";
    private static final String RECTANGLE_GROUP = "Rectangle Analysis Commands";

    private static final List<String> PREFERRED_GROUP_ORDER = List.of(
            RECTANGLE_GROUP,
            BUILT_IN_GROUP
    );

    // The subset of Spring Shell built-ins that still make sense when the
    // process will exit immediately after running a single command. Names
    // match Command.getName() values registered by
    // StandardCommandsAutoConfiguration (Help and Version).
    private static final Set<String> ONE_SHOT_ALLOWED_BUILT_INS = Set.of("help", "version");

    private final boolean interactiveEnabled;

    public RectangleHelpCommand(
            @Value("${spring.shell.interactive.enabled:false}") boolean interactiveEnabled) {
        this.interactiveEnabled = interactiveEnabled;
    }

    // Command.getName() derives the command name from the simple class name
    // (lowercased), which for us would be "rectanglehelpcommand". The
    // superclass Help relies on that default because its class is literally
    // named "Help" — we have to re-pin the name to "help" explicitly so the
    // registry can still dispatch `help` to this bean.
    @Override
    public String getName() {
        return "help";
    }

    @Override
    public ExitStatus execute(CommandContext commandContext) throws Exception {
        List<CommandArgument> args = commandContext.parsedInput().arguments();
        boolean hasCommandArgument = args.stream()
                .map(CommandArgument::value)
                .anyMatch(value -> value != null && !value.isBlank());

        // Per-command help (e.g. `help analyze`) uses the superclass's
        // NAME/SYNOPSIS/OPTIONS renderer unchanged.
        if (hasCommandArgument) {
            return super.execute(commandContext);
        }

        PrintWriter out = commandContext.outputWriter();
        out.println(formatAvailable(commandContext.commandRegistry()));
        out.flush();
        return ExitStatus.OK;
    }

    private String formatAvailable(CommandRegistry registry) {
        Set<Command> commands = new HashSet<>(registry.getCommands());
        commands.add(Utils.QUIT_COMMAND);

        Predicate<Command> visible = c -> !c.isHidden() && isRelevantForCurrentMode(c);

        Comparator<String> groupOrder = Comparator
                .<String>comparingInt(group -> {
                    int i = PREFERRED_GROUP_ORDER.indexOf(group);
                    return i < 0 ? Integer.MAX_VALUE : i;
                })
                .thenComparing(Comparator.naturalOrder());

        List<String> groups = commands.stream()
                .filter(visible)
                .map(Command::getGroup)
                .distinct()
                .sorted(groupOrder)
                .toList();

        String newline = System.lineSeparator();
        StringBuilder sb = new StringBuilder("AVAILABLE COMMANDS")
                .append(newline)
                .append(newline);

        for (String group : groups) {
            sb.append(group).append(newline);
            commands.stream()
                    .filter(visible)
                    .filter(c -> c.getGroup().equals(group))
                    .sorted(Comparator.comparing(Command::getName))
                    .forEach(c -> sb.append('\t')
                            .append(c.getName())
                            .append(c.getAliases().isEmpty() ? "" : ", " + String.join(", ", c.getAliases()))
                            .append(": ")
                            .append(c.getDescription())
                            .append(newline));
        }
        return sb.toString();
    }

    private boolean isRelevantForCurrentMode(Command command) {
        if (interactiveEnabled) {
            return true;
        }
        if (!BUILT_IN_GROUP.equals(command.getGroup())) {
            return true;
        }
        return ONE_SHOT_ALLOWED_BUILT_INS.contains(command.getName());
    }
}
