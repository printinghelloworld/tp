package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.*;

import java.util.Arrays;
import java.util.stream.Stream;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.assignment.*;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class FindCommandParser implements Parser<FindCommand> {

    private static boolean isDateFormat(String keyword) {
        return keyword.matches("\\d{2}-\\d{2}-\\d{4}");
    }

    private static boolean isTimeFormat(String keyword) {
        return keyword.matches("\\d{4}");
    }

    private static boolean moreThanOnePrefixPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        long countPrefixesPresent = Stream.of(prefixes)
                .filter(prefix -> argumentMultimap.getValue(prefix).isPresent()).count();
        return countPrefixesPresent > 1;
    }

    private static FindCommand findByName(String[] keywords) throws ParseException {
        for (String keyword : keywords) {
            ParserUtil.parseName(keyword);
        }
        return new FindCommand(new NameContainsKeywordsPredicate(Arrays.asList(keywords)));
    }

    private static FindCommand findByModuleCode(String[] keywords) throws ParseException {
        for (String keyword : keywords) {
            ParserUtil.parseModuleCode(keyword);
        }
        return new FindCommand(new ModuleCodeContainsKeywordsPredicate(Arrays.asList(keywords)));
    }

    private static FindCommand findByDeadline(String[] keywords) throws ParseException {
        for (String keyword : keywords) {
            boolean isNotTimeOrDateFormat = !(isTimeFormat(keyword) || isDateFormat(keyword));
            if (isNotTimeOrDateFormat) {
                throw new ParseException(
                        String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.INVALID_DATE_OR_TIME_MESSAGE));
            }
        }
        return new FindCommand(new DeadlineContainsKeywordsPredicate(Arrays.asList(keywords)));
    }

    private static FindCommand findByPriority(String[] keywords) throws ParseException {
        for (String keyword : keywords) {
            ParserUtil.parsePriority(keyword);
        }
        return new FindCommand(new PriorityContainsKeywordsPredicate(Arrays.asList(keywords)));
    }

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns a FindCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public FindCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String[] keywords;
        String trimmedArgs = args.trim();
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_DEADLINE, PREFIX_MODULE_CODE, PREFIX_PRIORITY);

        boolean isPreambleMissing = argMultimap.getPreamble().isEmpty();
        boolean isPrefixNamePresent = argMultimap.getValue(PREFIX_NAME).isPresent();
        boolean isPrefixDeadlinePresent = argMultimap.getValue(PREFIX_DEADLINE).isPresent();
        boolean isPrefixModuleCodePresent = argMultimap.getValue(PREFIX_MODULE_CODE).isPresent();
        boolean isPrefixPriorityPresent = argMultimap.getValue(PREFIX_PRIORITY).isPresent();

        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        } else if (moreThanOnePrefixPresent(argMultimap, PREFIX_NAME, PREFIX_MODULE_CODE, PREFIX_DEADLINE, PREFIX_PRIORITY)) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MORE_THAN_ONE_PREFIX_MESSAGE));
        } else if (isPrefixNamePresent && isPreambleMissing) {
            keywords = argMultimap.getValue(PREFIX_NAME).get().split("\\s+");
            return findByName(keywords);
        } else if (isPrefixModuleCodePresent && isPreambleMissing) {
            keywords = argMultimap.getValue(PREFIX_MODULE_CODE).get().split("\\s+");
            return findByModuleCode(keywords);
        } else if (isPrefixDeadlinePresent && isPreambleMissing) {
            keywords = argMultimap.getValue(PREFIX_DEADLINE).get()
                    .split("\\s+");
            return findByDeadline(keywords);
        } else if (isPrefixPriorityPresent && isPreambleMissing) {
            keywords = argMultimap.getValue(PREFIX_PRIORITY).get().split("\\s+");
            return findByPriority(keywords);
        } else {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }
    }
}
