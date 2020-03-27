/*!
 * Bootstrap-select v1.13.9 (https://developer.snapappointments.com/bootstrap-select)
 *
 * Copyright 2012-2019 SnapAppointments, LLC
 * Licensed under MIT (https://github.com/snapappointments/bootstrap-select/blob/master/LICENSE)
 */

!function(e,t){void 0===e&&void 0!==window&&(e=window),"function"==typeof define&&define.amd?define(["jquery"],function(e){return t(e)}):"object"==typeof module&&module.exports?module.exports=t(require("jquery")):t(e.jQuery)}(this,function(e){e.fn.selectpicker.defaults={noneSelectedText:"Odaberite stavku",noneResultsText:"Nema rezultata pretrage {0}",countSelectedText:function(e,t){return 1==e?"{0} stavka selektirana":"{0} stavke selektirane"},maxOptionsText:function(e,t){return[1==e?"Limit je postignut ({n} stvar maximalno)":"Limit je postignut ({n} stavke maksimalno)",1==t?"Grupni limit je postignut ({n} stvar maksimalno)":"Grupni limit je postignut ({n} stavke maksimalno)"]},selectAllText:"Selektiraj sve",deselectAllText:"Deselektiraj sve",multipleSeparator:", "}});