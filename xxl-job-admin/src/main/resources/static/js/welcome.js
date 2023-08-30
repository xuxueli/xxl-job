$(function () {
    $('#sysDate').text(nowDate());
    let account = sessionStorage.getItem('currentUser');
    $("#current").text(account);

    let home = get("/home");
    if (!_.isEmpty(home)) {
        let total = Number(home.keystoreTotal) + Number(home.projectTotal) + Number(home.licenseTotal) + Number(home.userInfoTotal);
        $("#total").text(total);
        $("#total2").text(total);
        $("#user").text(home.userInfoTotal);
        $("#keystore").text(home.keystoreTotal);
        $("#project").text(home.projectTotal);
        $("#license").text(home.licenseTotal);
    }
})















