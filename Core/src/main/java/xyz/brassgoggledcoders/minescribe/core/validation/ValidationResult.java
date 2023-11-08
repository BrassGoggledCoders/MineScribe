package xyz.brassgoggledcoders.minescribe.core.validation;

public abstract class ValidationResult {

    public abstract boolean isValid();

    public abstract String getMessage();

    public static Valid valid() {
        return Valid.INSTANCE;
    }

    public static Error error(String message) {
        return new Error(message);
    }

    public static class Error extends ValidationResult {
        private final String message;

        public Error(String message) {
            this.message = message;
        }

        @Override
        public boolean isValid() {
            return false;
        }

        @Override
        public String getMessage() {
            return this.message;
        }
    }

    public static class Valid extends ValidationResult {
        public static Valid INSTANCE = new Valid();

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public String getMessage() {
            throw new IllegalStateException("Result is Valid");
        }
    }
}
