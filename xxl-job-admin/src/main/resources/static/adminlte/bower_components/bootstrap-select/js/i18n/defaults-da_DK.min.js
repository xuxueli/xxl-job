/*!
 * Bootstrap-select v1.13.9 (https://developer.snapappointments.com/bootstrap-select)
 *
 * Copyright 2012-2019 SnapAppointments, LLC
 * Licensed under MIT (https://github.com/snapappointments/bootstrap-select/blob/master/LICENSE)
 */

!function(e,n){void 0===e&&void 0!==window&&(e=window),"function"==typeof define&&define.amd?define(["jquery"],function(e){return n(e)}):"object"==typeof module&&module.exports?module.exports=n(require("jquery")):n(e.jQuery)}(this,function(e){e.fn.selectpicker.defaults={noneSelectedText:"Intet valgt",noneResultsText:"Ingen resultater fundet {0}",countSelectedText:function(e,n){return"{0} valgt"},maxOptionsText:function(e,n){return[1==e?"Begr\xe6nsning n\xe5et (max {n} valgt)":"Begr\xe6nsning n\xe5et (max {n} valgte)",1==n?"Gruppe-begr\xe6nsning n\xe5et (max {n} valgt)":"Gruppe-begr\xe6nsning n\xe5et (max {n} valgte)"]},selectAllText:"Mark\xe9r alle",deselectAllText:"Afmark\xe9r alle",multipleSeparator:", "}});