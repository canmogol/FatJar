package fatjar;


import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public interface Cond {

    static Hash hash() {
        return new Hash();
    }

    static Tree tree() {
        return new Tree();
    }

    static <T> OptionalConsumer<T> optional(Optional<T> optional) {
        return new OptionalConsumer<T>(optional);
    }

    class Hash {

        private static Logger logger = Logger.getLogger(Hash.class.getName());
        private CondException condException = (e) -> {
            logger.log(Level.SEVERE, "got exception while hash cond, error: " + e, e);
        };
        private Map<Object, Unit> hash = new HashMap<>();
        private Unit defaultUnit = Unit.empty();

        public Hash check(Object key, Unit unit) {
            hash.put(key, unit);
            return this;
        }

        public Hash otherwise(Unit unit) {
            this.defaultUnit = unit;
            return this;
        }

        public Hash error(CondException condException) {
            this.condException = condException;
            return this;
        }

        private void handleError(Exception e) {
            condException.apply(e);
        }

        public void eval(Object evalKey) {
            try {
                if (hash.containsKey(evalKey)) {
                    hash.get(evalKey).apply();
                } else {
                    defaultUnit.apply();
                }
            } catch (Exception e) {
                handleError(e);
            }
        }

    }

    class Tree {

        private static Logger logger = Logger.getLogger(Tree.class.getName());
        private CondException condException = (e) -> {
            logger.log(Level.SEVERE, "got exception while tree cond, error: " + e, e);
        };
        private List<CheckUnitPair> checkUnitPairs = new LinkedList<>();
        private Unit defaultUnit = Unit.empty();

        private Tree() {
        }

        public Tree check(Check check, Unit unit) {
            checkUnitPairs.add(new CheckUnitPair(check, unit));
            return this;
        }

        public Tree otherwise(Unit unit) {
            this.defaultUnit = unit;
            return this;
        }

        public Tree error(CondException condException) {
            this.condException = condException;
            return this;
        }

        private void handleError(Exception e) {
            condException.apply(e);
        }

        public void eval() {
            Optional<CheckUnitPair> cupOptional = checkUnitPairs
                    .stream()
                    .filter(cup -> {
                        try {
                            return cup.check.apply();
                        } catch (Exception e) {
                            cup.exception = e;
                            return true;
                        }
                    })
                    .findFirst();

            try {
                if (cupOptional.isPresent()) {
                    handleCup(cupOptional);
                } else {
                    defaultUnit.apply();
                }
            } catch (Exception e) {
                handleError(e);
            }
        }

        private void handleCup(Optional<CheckUnitPair> cupOptional) throws Exception {
            CheckUnitPair cup = cupOptional.get();
            if (cup.exception == null) {
                cup.unit.apply();
            } else {
                throw cup.exception;
            }
        }

        class CheckUnitPair {
            private Check check;
            private Unit unit;
            private Exception exception;

            private CheckUnitPair(Check check, Unit unit) {
                this.check = check;
                this.unit = unit;
            }
        }
    }

    static class OptionalConsumer<T> {
        private final Optional<T> optional;
        private CondException condException = (e) -> Log.error("got exception while optinal cond, error: " + e);
        private Consumer<T> ifPresent;
        private Unit orElse;

        public OptionalConsumer(Optional<T> optional) {
            this.optional = optional;
        }

        public OptionalConsumer<T> ifPresent(Consumer<T> ifPresent) {
            this.ifPresent = ifPresent;
            return this;
        }

        public OptionalConsumer<T> orElse(Unit orElse) {
            this.orElse = orElse;
            return this;
        }

        public OptionalConsumer<T> error(CondException condException) {
            this.condException = condException;
            return this;
        }

        public void eval() {
            try {
                if (optional.isPresent()) {
                    this.ifPresent.accept(optional.get());
                } else {
                    this.orElse.apply();
                }
            } catch (Exception e) {
                handleError(e);
            }
        }

        private void handleError(Exception e) {
            condException.apply(e);
        }

    }

    @FunctionalInterface
    interface CondException {
        void apply(Exception e);
    }

    @FunctionalInterface
    interface Unit {
        static Unit empty() {
            return () -> {
            };
        }

        void apply();
    }

    @FunctionalInterface
    interface Check {
        boolean apply();
    }
}
