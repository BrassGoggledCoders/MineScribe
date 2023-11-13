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
                return validationHelper.createErrorResult("Field should " + filledIn + " fields, should only be " + storedValues['number'])
            } else {
                return validationHelper.createValidResult()
            }
        },
        [
            minescribe.fieldHelper.ofStringList("fields", null),
            minescribe.fieldHelper.ofInt("number", 1)
        ]
    )
)