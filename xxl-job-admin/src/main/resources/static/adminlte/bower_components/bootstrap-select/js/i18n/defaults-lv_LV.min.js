/*!
 * Bootstrap-select v1.13.9 (https://developer.snapappointments.com/bootstrap-select)
 *
 * Copyright 2012-2019 SnapAppointments, LLC
 * Licensed under MIT (https://github.com/snapappointments/bootstrap-select/blob/master/LICENSE)
 */

!function(e,t){void 0===e&&void 0!==window&&(e=window),"function"==typeof define&&define.amd?define(["jquery"],function(e){return t(e)}):"object"==typeof module&&module.exports?module.exports=t(require("jquery")):t(e.jQuery)}(this,function(e){e.fn.selectpicker.defaults={noneSelectedText:"Nekas nav atz\u012bm\u0113ts",noneResultsText:"Nav neviena rezult\u0101ta {0}",countSelectedText:function(e,t){return 1==e?"{0} ieraksts atz\u012bm\u0113ts":"{0} ieraksti atz\u012bm\u0113ts"},maxOptionsText:function(e,t){return[1==e?"Sasniegts limits ({n} ieraksts maksimums)":"Sasniegts limits ({n} ieraksti maksimums)",1==t?"Sasniegts grupas limits ({n} ieraksts maksimums)":"Sasniegts grupas limits ({n} ieraksti maksimums)"]},selectAllText:"Atz\u012bm\u0113t visu",deselectAllText:"Neatz\u012bm\u0113t nevienu",multipleSeparator:", "}});