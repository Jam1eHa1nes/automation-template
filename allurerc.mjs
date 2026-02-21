/** @type {import('allure').GlobalConfig} */
export default {
    name: "Automation Template",
    output: "allure-report",
    plugins: {
        awesome: {
            options: {
                reportLanguage: "en",
                singleFile: false,
            },
        },
        allure2: {
            enabled: false,
        },
    },
};
