$(document).ready(function(){
    $("#emailInput").keyup(function(){
        positionChange();
        $("#endCheck").attr("value",0);
    });
});

function addMember(){
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    const email = $("#emailInput").val();
    const pass = $("#passInput").val();
    const name = $("#nameInput").val();
    const phone = $("#phoneInput").val();
    const address1 = $("#post_Address").val();
    const address2 = $("#main_Address").val();
    const address3 = $("#detail_Address").val();
    const check = $("#endCheck").val();

    let data = {
        "email" : email,
        "password" : pass,
        "name" : name,
        "phone" : phone,
        "address1" : address1,
        "address2" : address2,
        "address3" : address3,
        "check" : check
    };
    var param = JSON.stringify(data);

    $.ajax({
        url:"/members/new",
        type:"POST",
        contentType : "application/json; charset=utf-8",
        data:param,
        beforeSend : function(xhr){
            xhr.setRequestHeader(header, token);
        },
        success: function(resp){
            if (resp.status == 400) {
                if (resp.data.hasOwnProperty('valid_email')) {
                    $('#valid_email').text(resp.data.valid_email);
                } else {
                    $('#valid_email').text('');
                }

                if (resp.data.hasOwnProperty('valid_password')) {
                    $('#valid_password').text(resp.data.valid_password);
                } else {
                    $('#valid_password').text('');
                }

                if (resp.data.hasOwnProperty('valid_name')) {
                    $('#valid_name').text(resp.data.valid_name);
                } else {
                    $('#valid_name').text('');
                }

                if (resp.data.hasOwnProperty('valid_phone')) {
                    $('#valid_phone').text(resp.data.valid_phone);
                } else {
                    $('#valid_phone').text('');
                }

                if (resp.data.hasOwnProperty('valid_address1') || resp.data.hasOwnProperty('valid_address2') || resp.data.hasOwnProperty('valid_address3')) {
                    $('#valid_address').text(resp.data.valid_address1);
                    $('#valid_address').text(resp.data.valid_address2);
                    $('#valid_address').text(resp.data.valid_address3);
                } else {
                    $('#valid_address').text('');
                }
            }
            else if(resp.status == 409) {
                alert("이메일 중복검사를 해주세요!");
            }
            else {
                alert("회원가입이 완료되었습니다.");
                location.href = "/members/login";
            }
        },
        error : function(jqXHR, status, error){
            alert(jqXHR.responseText);
        }
    });
}

function sendEmail(){
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    const userId = $("#emailInput").val();
    const input_msg = $("#id_CheckInput");
    const email = $("#email_key");
    const email_btn = $("#email_btn");

    let paramData = {};
    paramData["mail"] = userId;

    var param = JSON.stringify(paramData);

    $.ajax({
        url:"/members/idCheck",
        type:"post",
        contentType : "application/json",
        dataType:"json",
        data:param,
        beforeSend : function(xhr){
            xhr.setRequestHeader(header, token);
        },
        success: function(data){
            if(data == 1){
                input_msg.text("중복된 아이디입니다.");
                input_msg.css("color","red");
            }
            else{
                input_msg.text("사용가능한 아이디입니다.");
                startSpinner();
                input_msg.css("color","green");
                $.ajax({
                    url:"/api/mail",
                    type:"post",
                    contentType : "application/json",
                    dataType:"json",
                    data:param,
                    beforeSend : function(xhr){
                        xhr.setRequestHeader(header, token);
                    },
                    success: function(data){
                        stopSpinner();
                        alert("인증번호 발송 완료!");
                        $("#confirm").attr("value",data);
                        $("#email_btn").css("display","none");
                        $("#email_key").css("display","block");
                    },
                    error : function(jqXHR, status, error){
                        stopSpinner();
                        alert("잘못된 이메일입니다. 다시입력해주세요.");
                        $('#emailInput').val('');
                        input_msg.text('');
                    }
                });
            }
        },
        error : function(jqXHR, status, error){
            alert(jqXHR.responseText);
        }
    });
}

function sendNumber(){
    var number1 = $("#number").val();
    var number2 = $("#confirm").val();

    if(number1 == number2){
        alert("인증되었습니다.");
        $("#endCheck").attr("value",1);
    }
    else{
        alert("실패하였습니다.");
    }
}

function show_form(){
    const login = document.getElementById("log_form");
    const sign = document.getElementById("sign_form");
    const log_h2 = document.getElementById("log_h2");
    const sign_h2 = document.getElementById("sign_h2");
    const check = document.getElementById("check");
    const is_checked = check.checked;

    if(is_checked){
        login.style.display = 'none';
        sign.style.display = 'block';
        log_h2.style.opacity = '10%';
        sign_h2.style.opacity = '100%';

    }
    else{
        login.style.display = 'block';
        sign.style.display = 'none';
        log_h2.style.opacity = '100%';
        sign_h2.style.opacity = '10%';
    }
}

function positionChange(){
    const email = $("#email_key");
    const email_btn = $("#email_btn");
    const input_msg = $("#id_CheckInput");
    email_btn.css("display","block")
    email.css("display","none");
    input_msg.text("");
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

function login(){
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    let data = {
        "user_email" : $('#email').val(),
        "user_pass" : $('#pass').val()
    };
    $.ajax({
        url : "/members/loginTry",
        type : 'POST',
        data : data,
        beforeSend : function(xhr){
            xhr.setRequestHeader(header, token);
        },
        success : function(response){
            if(response == "/"){
                location.href=response;
            }
            else{
                $('#login_Error').text(response);
            }
        },
        error : function(response){
            alert(response);
        }
    });
}