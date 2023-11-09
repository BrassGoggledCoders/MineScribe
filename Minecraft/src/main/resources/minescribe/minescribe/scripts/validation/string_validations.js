const validationHelper = minescribe.validationHelper

const regexValidation = validationHelper.create(
    function (fieldValue, storedValues) {
        const regex = new RegExp(storedValues.regex)
        if (regex.test(fieldValue)) {
            return validationHelper.createValidResult();
        } else {
            return validationHelper.createErrorResult(object + " did not match " + storedValues.regex)
        }
    },
    [
        minescribe.fieldHelper.ofString("regex", null)
    ]
)

minescribe.validationRegistry.register(
    "minescribe:regex",
    regexValidation
)