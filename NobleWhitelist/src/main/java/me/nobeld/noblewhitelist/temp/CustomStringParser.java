package me.nobeld.noblewhitelist.temp;

import org.incendo.cloud.caption.CaptionVariable;
import org.incendo.cloud.caption.StandardCaptionKeys;
import org.incendo.cloud.component.CommandComponent;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.exception.parsing.ParserException;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.parser.standard.StringParser;
import org.jetbrains.annotations.NotNull;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomStringParser<C> implements ArgumentParser<C, String> { // TODO regex validation
    private static final Pattern QUOTED_DOUBLE = Pattern.compile("\"(?<inner>(?:[^\"\\\\]|\\\\.)*)\"");
    private static final Pattern QUOTED_SINGLE = Pattern.compile("'(?<inner>(?:[^'\\\\]|\\\\.)*)'");

    public static <C> ParserDescriptor<C, String> customStringParser() {
        return ParserDescriptor.of(new CustomStringParser<>(), String.class);
    }

    public static <C> CommandComponent.Builder<C, String> customStringComponent() {
        return CommandComponent.<C, String>builder().parser(customStringParser());
    }

    @NotNull
    @Override
    public ArgumentParseResult<String> parse(@NotNull CommandContext<C> context, @NotNull CommandInput commandInput) {
        final char peek = commandInput.peek();
        if (peek != '\'' && peek != '"') {
            String str = commandInput.readString();
            if (str.contains(" ")) {
                return ArgumentParseResult.failure(new WhitespaceStringParseException(context));
            }
            return ArgumentParseResult.success(str);
        }

        final String string = commandInput.remainingInput();

        final Matcher doubleMatcher = QUOTED_DOUBLE.matcher(string);
        String doubleMatch = null;
        if (doubleMatcher.find()) {
            doubleMatch = doubleMatcher.group("inner");
        }
        final Matcher singleMatcher = QUOTED_SINGLE.matcher(string);
        String singleMatch = null;
        if (singleMatcher.find()) {
            singleMatch = singleMatcher.group("inner");
        }

        String inner = null;
        if (singleMatch != null && doubleMatch != null) {
            final int singleIndex = string.indexOf(singleMatch);
            final int doubleIndex = string.indexOf(doubleMatch);
            inner = doubleIndex < singleIndex ? doubleMatch : singleMatch;
        } else if (singleMatch == null && doubleMatch != null) {
            inner = doubleMatch;
        } else if (singleMatch != null) {
            inner = singleMatch;
        }

        if (inner != null) {
            commandInput.readString();
        } else {
            inner = commandInput.peekString();
            if (inner.startsWith("\"") || inner.startsWith("'")) {
                return ArgumentParseResult.failure(new StringParser.StringParseException(
                        commandInput.remainingInput(),
                        StringParser.StringMode.QUOTED, context
            ));
            } else {
                commandInput.readString();
            }
        }

        inner = inner.replace("\\\"", "\"").replace("\\'", "'");

        if (inner.contains(" ")) {
            return ArgumentParseResult.failure(new WhitespaceStringParseException(context));
        }

        return ArgumentParseResult.success(inner);
    }

    public static final class WhitespaceStringParseException extends ParserException {
        public WhitespaceStringParseException(CommandContext<?> context) {
            super(CustomStringParser.class, context, StandardCaptionKeys.EXCEPTION_INVALID_ARGUMENT, CaptionVariable.of("cause", "Input must not contain whitespaces"));
        }
    }
}
