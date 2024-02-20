$(document).ready(function(){
    $('#passBtn').on('click',function(){
        passClick();
    });
});

function passClick(){
    var pass = prompt("비밀번호를 입력해주세요.");
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");

    let data = {
        pass : pass,
    };
    var param = JSON.stringify(data);
    $.ajax({
        url : "/members/passCheck",
        type : 'POST',
        data : param,
        contentType : "application/json",
        dataType:"json",
        beforeSend : function(xhr){
            xhr.setRequestHeader(header, token);
        },
        success : function(response){
            location.href='/members/changePass';
        },
        error : function(jqXHR, status, error){
            if(jqXHR.status == '401'){
                alert('로그인 후 이용해주세요.');
            }
            if(jqXHR.status == '400'){
                alert("비밀번호가 일치하지 않습니다.");
            }
            else{
                alert(jqXHR.responseText);
            }
        }
    });
}

function address_Btn() {
    new daum.Postcode({
        oncomplete: function(data) {
            var addr = '';

            if (data.userSelectedType === 'R') {
                addr = data.roadAddress;
            } else {
                addr = data.jibunAddress;
            }

            document.getElementById('post_Address').value = data.zonecode;
            document.getElementById("main_Address").value = addr;
            document.getElementById("detail_Address").focus();
        }
    }).open();
}