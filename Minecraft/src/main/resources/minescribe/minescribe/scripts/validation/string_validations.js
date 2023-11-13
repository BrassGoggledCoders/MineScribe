// noinspection JSUnresolvedReference
minescribe.validationRegistry.register(
    "minescribe:regex",
    validationHelper.createForField(
        function (fieldValue, storedValues) {
            const regex = new RegExp(storedValues['regex'])
            if (regex.test(fieldValue)) {
                // noinspection JSUnresolvedReference
                return validationHelper.createValidResult();
            } else {
                // noinspection JSUnresolvedReference
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
            // noinspection JSUnresolvedReference
            if (fieldValue.length >= storedValues['minLength']) {
                // noinspection JSUnresolvedReference
                return validationHelper.createValidResult();
            } else {
                // noinspection JSUnresolvedReference
                return validationHelper.createErrorResult(fieldValue + " must be at least " + storedValues['minLength'] + " long")
            }
        },
        [
            minescribe.fieldHelper.ofInt("minLength", 0)
        ]
    )
)