$(document).ready(function(){
    $('#passBtn').on("click",function(){
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        var pass = $('#pass').val();
        var passRe = $('#passRe').val();
        if(pass != passRe){
            $('#error').text("비밀번호가 일치하지 않습니다.");
            return;
        }
        var passValid = $.trim(pass);
        if(passValid.length<4 || passValid.length>8){
            $('#error').text("비밀번호 양식이 맞지 않습니다.");
            return;
        }
        let data = {
            pass : pass,
        };
        var param = JSON.stringify(data);

        $.ajax({
            url : '/members/changePass',
            type : "POST",
            contentType : "application/json",
            data : param,
            dataType : "JSON",
            beforeSend : function(xhr){
                xhr.setRequestHeader(header, token);
            },
            success : function(data){
                location.href='/';
            },
            error : function(jqXHR, status, error){
                if(jqXHR.status == '401'){
                    alert('로그인 후 이용해주세요.');
                    location.href='/members/login';
                }
                else{
                    alert(jqXHR.responseText);
                }
            }
        });

    });
});