export {}

declare namespace minescribe {
    const validationRegistry: ValidationRegistry
    const validationHelper: ValidationHelper
    const fieldHelper: FieldHelper


    interface Codec<T> {

    }

    interface FieldValidation {
        validate(value: object): boolean
    }

    interface ValidationRegistry {
        register(name: string, value: Codec<FieldValidation>): void
    }

    interface ValidationHelper {
        createForField(validation: (value: object, storedValues: object) => ValidationResult, fields: Field[]): Codec<FieldValidation>

        createErrorResult(message: string): ValidationResult

        createValidResult(): ValidationResult
    }

    interface FieldHelper {
        ofString(name: string, defaultValue: string | null): Field
    }

    interface Field {

    }

    interface ValidationResult {

    }

}

