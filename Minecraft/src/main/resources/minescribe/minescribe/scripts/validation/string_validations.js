// noinspection JSUnresolvedReference
const validationHelper = minescribe.validationHelper

// noinspection JSUnresolvedReference
minescribe.validationRegistry.register(
    "minescribe:regex",
    validationHelper.createForField(
        function (fieldValue, storedValues) {
            const regex = new RegExp(storedValues['regex'])
            if (regex.test(fieldValue)) {
                return validationHelper.createValidResult();
            } else {
                return validationHelper.createErrorResult(fieldValue + " does not match " + storedValues['regex'])
            }
        },
        [
            minescribe.fieldHelper.ofString("regex", null)
        ]
    )
)

// noinspection JSUnresolvedReference
minescribe.validationRegistry.register(
    "minescribe:min_length",
    validationHelper.createForField(
        function (fieldValue, storedValues) {
            if (fieldValue.length >= storedValues['minLength']) {
                return validationHelper.createValidResult();
            } else {
                return validationHelper.createErrorResult(fieldValue + " must be at least " + storedValues['minLength'] + " long")
            }
        },
        [
            minescribe.fieldHelper.ofInt("minLength", 0)
        ]
    )
)