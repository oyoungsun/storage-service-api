// radio-url-handler.js

$(document).ready(function() {
    // 라디오 박스가 변경될 때마다 URL에 kioskType 추가
    $('input[name="kioskType"]').change(function() {
        updateActionUrl();
    });

    // 페이지 로딩 시에도 URL에 kioskType 추가
    updateActionUrl();
});

function updateActionUrl() {
    var kioskType = $('input[name="kioskType"]:checked').val();
    var actionUrl = "/api/v1/app_upload/" + (kioskType ? kioskType : "");

    $('#uploadForm').attr('action', actionUrl);
}