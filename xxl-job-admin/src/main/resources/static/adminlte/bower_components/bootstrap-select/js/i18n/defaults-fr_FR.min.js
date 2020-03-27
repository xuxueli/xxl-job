/*!
 * Bootstrap-select v1.13.9 (https://developer.snapappointments.com/bootstrap-select)
 *
 * Copyright 2012-2019 SnapAppointments, LLC
 * Licensed under MIT (https://github.com/snapappointments/bootstrap-select/blob/master/LICENSE)
 */

!function(e,t){void 0===e&&void 0!==window&&(e=window),"function"==typeof define&&define.amd?define(["jquery"],function(e){return t(e)}):"object"==typeof module&&module.exports?module.exports=t(require("jquery")):t(e.jQuery)}(this,function(e){e.fn.selectpicker.defaults={noneSelectedText:"Aucune s\xe9lection",noneResultsText:"Aucun r\xe9sultat pour {0}",countSelectedText:function(e,t){return 1<e?"{0} \xe9l\xe9ments s\xe9lectionn\xe9s":"{0} \xe9l\xe9ment s\xe9lectionn\xe9"},maxOptionsText:function(e,t){return[1<e?"Limite atteinte ({n} \xe9l\xe9ments max)":"Limite atteinte ({n} \xe9l\xe9ment max)",1<t?"Limite du groupe atteinte ({n} \xe9l\xe9ments max)":"Limite du groupe atteinte ({n} \xe9l\xe9ment max)"]},multipleSeparator:", ",selectAllText:"Tout s\xe9lectionner",deselectAllText:"Tout d\xe9s\xe9lectionner"}});