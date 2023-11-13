// noinspection JSUnresolvedReference
minescribe.validationRegistry.register(
    "minescribe:only_x",
    validationHelper.createForForm(
        function (formValues, storedValues) {
            const fields = storedValues['fields']
            let filledIn = 0
            for (const field of fields) {
                if (formValues[field] != null) {
                    filledIn++
                }
            }
            if (filledIn !== storedValues['number']) {
                // noinspection JSUnresolvedReference
                return validationHelper.createErrorResult("Field contains " + filledIn + " filled fields, should only be " + storedValues['number'])
            } else {
                // noinspection JSUnresolvedReference
                return validationHelper.createValidResult()
            }
        },
        [
            minescribe.fieldHelper.ofStringList("fields", null),
            minescribe.fieldHelper.ofInt("number", 1)
        ]
    )
)