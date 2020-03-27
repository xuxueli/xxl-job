/*!
 * Bootstrap-select v1.13.9 (https://developer.snapappointments.com/bootstrap-select)
 *
 * Copyright 2012-2019 SnapAppointments, LLC
 * Licensed under MIT (https://github.com/snapappointments/bootstrap-select/blob/master/LICENSE)
 */

!function(e,t){void 0===e&&void 0!==window&&(e=window),"function"==typeof define&&define.amd?define(["jquery"],function(e){return t(e)}):"object"==typeof module&&module.exports?module.exports=t(require("jquery")):t(e.jQuery)}(this,function(e){e.fn.selectpicker.defaults={noneSelectedText:"Ei valintoja",noneResultsText:"Ei hakutuloksia {0}",countSelectedText:function(e,t){return 1==e?"{0} valittu":"{0} valitut"},maxOptionsText:function(e,t){return["Valintojen maksimim\xe4\xe4r\xe4 ({n} saavutettu)","Ryhm\xe4n maksimim\xe4\xe4r\xe4 ({n} saavutettu)"]},selectAllText:"Valitse kaikki",deselectAllText:"Poista kaikki",multipleSeparator:", "}});