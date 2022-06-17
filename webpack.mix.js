const mix = require("laravel-mix")

/**
 * Transfers Module
 */
mix.js("sctp-mis/src/main/javascript/transfers/main.js", "assets/js/transfers")
    .setPublicPath("sctp-mis/src/main/resources/public")
    .vue({ version: 3 })
