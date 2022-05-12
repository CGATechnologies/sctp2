/**
 * UI code for the Transfers Periods Page using VueJS
 */
 function domready(fn) {
   if (document.readyState != 'loading'){
     fn();
   } else {
     document.addEventListener('DOMContentLoaded', fn);
   }
 }

domready(() => {
    if (var el = document.getElementById("transfers:CreateNewPeriodApp")) {
        var transfersPeriodsApp = Vue.createApp({
            data: () => {
                return {
                    showPreview: false,
                    availableMonths: [],
                }
            },
            methods: {

            }
        });

        transfersPeriodsApp.mount(el);
    }
});
