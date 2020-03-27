/*!
 * Bootstrap-select v1.13.9 (https://developer.snapappointments.com/bootstrap-select)
 *
 * Copyright 2012-2019 SnapAppointments, LLC
 * Licensed under MIT (https://github.com/snapappointments/bootstrap-select/blob/master/LICENSE)
 */

!function(e,t){void 0===e&&void 0!==window&&(e=window),"function"==typeof define&&define.amd?define(["jquery"],function(e){return t(e)}):"object"==typeof module&&module.exports?module.exports=t(require("jquery")):t(e.jQuery)}(this,function(e){e.fn.selectpicker.defaults={noneSelectedText:"Bitte w\xe4hlen...",noneResultsText:"Keine Ergebnisse f\xfcr {0}",countSelectedText:function(e,t){return 1==e?"{0} Element ausgew\xe4hlt":"{0} Elemente ausgew\xe4hlt"},maxOptionsText:function(e,t){return[1==e?"Limit erreicht ({n} Element max.)":"Limit erreicht ({n} Elemente max.)",1==t?"Gruppen-Limit erreicht ({n} Element max.)":"Gruppen-Limit erreicht ({n} Elemente max.)"]},selectAllText:"Alles ausw\xe4hlen",deselectAllText:"Nichts ausw\xe4hlen",multipleSeparator:", "}});