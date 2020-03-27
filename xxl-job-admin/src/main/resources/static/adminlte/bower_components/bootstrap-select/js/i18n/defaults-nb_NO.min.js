/*!
 * Bootstrap-select v1.13.9 (https://developer.snapappointments.com/bootstrap-select)
 *
 * Copyright 2012-2019 SnapAppointments, LLC
 * Licensed under MIT (https://github.com/snapappointments/bootstrap-select/blob/master/LICENSE)
 */

!function(e,t){void 0===e&&void 0!==window&&(e=window),"function"==typeof define&&define.amd?define(["jquery"],function(e){return t(e)}):"object"==typeof module&&module.exports?module.exports=t(require("jquery")):t(e.jQuery)}(this,function(e){e.fn.selectpicker.defaults={noneSelectedText:"Ingen valgt",noneResultsText:"S\xf8ket gir ingen treff {0}",countSelectedText:function(e,t){return 1==e?"{0} alternativ valgt":"{0} alternativer valgt"},maxOptionsText:function(e,t){return["Grense n\xe5dd (maks {n} valg)","Grense for grupper n\xe5dd (maks {n} grupper)"]},selectAllText:"Merk alle",deselectAllText:"Fjern alle",multipleSeparator:", "}});