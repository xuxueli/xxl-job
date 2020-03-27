/*!
 * Bootstrap-select v1.13.9 (https://developer.snapappointments.com/bootstrap-select)
 *
 * Copyright 2012-2019 SnapAppointments, LLC
 * Licensed under MIT (https://github.com/snapappointments/bootstrap-select/blob/master/LICENSE)
 */

!function(e,i){void 0===e&&void 0!==window&&(e=window),"function"==typeof define&&define.amd?define(["jquery"],function(e){return i(e)}):"object"==typeof module&&module.exports?module.exports=i(require("jquery")):i(e.jQuery)}(this,function(e){e.fn.selectpicker.defaults={noneSelectedText:"Niekas nepasirinkta",noneResultsText:"Niekas nesutapo su {0}",countSelectedText:function(e,i){return 1==e?"{0} elementas pasirinktas":"{0} elementai(-\u0173) pasirinkta"},maxOptionsText:function(e,i){return[1==e?"Pasiekta riba ({n} elementas daugiausiai)":"Riba pasiekta ({n} elementai(-\u0173) daugiausiai)",1==i?"Grup\u0117s riba pasiekta ({n} elementas daugiausiai)":"Grup\u0117s riba pasiekta ({n} elementai(-\u0173) daugiausiai)"]},selectAllText:"Pasirinkti visus",deselectAllText:"Atmesti visus",multipleSeparator:", "}});