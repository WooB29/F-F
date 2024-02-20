$(document).ready(function(){
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    calculateTotalPrice();

    $("#count").change(function(){
        calculateTotalPrice();
    });
    var itemId = $('#itemId').val();
    $("#newBtn").click(function(){
        newComment(itemId, token, header);
    });

    $(".comment_btn").click(function(){
        const commentId = $(this).data('commentid');
        const row = $(this).closest("li");
        const content = row.find("input:eq(0)").val();
        const a = $(this).data('btn');
        let paramData = {
            commentId : commentId,
            content : content,
            itemId : itemId,
        };
        var param = JSON.stringify(paramData);

        if(a == 1){
            modifyComment(param, token, header);
        }
        else{
            deleteComment(param, token, header);
        }
    });

    var one = $('#comma1');
    one.text(priceToString(one.text()) + '원');
    var two = $('#comma2');
    two.text(priceToString(two.text()) + '원');
    var three = $('#comma3');
    three.text(priceToString(three.text()) +'원');
});

function calculateTotalPrice(){
    var count = $("#count").val();
    var price = $(".price").val();
    var totalPrice = price * count;

    $("#totalPrice").html(priceToString(totalPrice) + '원');
}

function uniqueNumber() {
    var timestamp = new Date().getTime();
    var randomNumber = Math.floor(Math.random() * 10000);
    return 'ORD' + timestamp + '-' + randomNumber;
}

function order(){
    var memberEmail = $('#memberEmail').val();
    var check = $.trim(memberEmail);
    if(check == ''){
        alert("로그인 후 이용해주세요.");
        return;
    }
    var itemId = $("#itemId").val();
    var count = parseInt($("#count").val());
    var stockNumber = parseInt($('#stockNumber').val());
    if(itemId == ''){
        alert("상품Id는 필수입력 입니다.");
        return;
    }
    if(count <1 || count > stockNumber){
        alert("남은 재고수 : "+stockNumber);
        return;
    }

    var uid = uniqueNumber();
    var itemNm = $('#itemNm').text();
    var total = $('#totalPrice').text();

    var IMP = window.IMP;
    IMP.init("imp63560331");
    IMP.request_pay({
        pg: "kakaopay.TC0ONETIME",
        pay_method: "card",
        merchant_uid: uid,
        name: itemNm,
        amount: total,
        buyer_email: memberEmail,
    }, function (rsp) {
        if(rsp.success){
            alert("결제 완료");
            var token = $("meta[name='_csrf']").attr("content");
            var header = $("meta[name='_csrf_header']").attr("content");
            var url = "/order";

            var paramData = {
                itemId : itemId,
                count : count
            }

            var param = JSON.stringify(paramData);
            $.ajax({
                url : url,
                type : "POST",
                contentType : "application/json",
                data : param,
                beforeSend : function(xhr){
                    xhr.setRequestHeader(header, token);
                },
                dataType : "json",
                cache : false,
                success : function(result, status){
                    alert("주문이 완료 되었습니다.");
                    location.href='/';
                },
                error : function(jqXHR, status, error){
                    var errorData = {
                        email : memberEmail,
                        code : jqXHR.status,
                        content : jqXHR.responseText,
                        location : "order",
                        comment : "결제완료"
                    }
                    $.ajax({
                        url : "/error/err",
                        type : "POST",
                        contentType : "application/json",
                        data : JSON.stringify(errorData),
                        beforeSend : function(xhr){
                            xhr.setRequestHeader(header, token);
                        },
                        cache : false,
                        success : function(result, status){
                            alert("오류발생\n관리자에게 문의주세요.");
                            location.href='/';
                        },
                        error : function(jqXHR, status, error){
                            alert(jqXHR.responseText);
                        }
                    });
                }
            });
        }
        else{
            alert("결제 실패");
        }
    });
}

function addCart(){
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    var count = parseInt($("#count").val());
    var stockNumber = parseInt($('#stockNumber').val());

    if(count < 1 || count > stockNumber){
        alert("재고 수 : "+stockNumber);
        return;
    }
    var url = "/cart";

    var paramData = {
        itemId : $("#itemId").val(),
        count : count
    };
    var param = JSON.stringify(paramData);

    $.ajax({
        url : url,
        type : "POST",
        contentType : "application/json",
        data : param,
        beforeSend : function(xhr){
            xhr.setRequestHeader(header, token);
        },
        dataType : "json",
        cache : false,
        success : function(result, status){
            alert("상품을 장바구니에 담았습니다.");
            location.href='/';
        },
        error : function(jqXHR, status, error){
            if(jqXHR.status == '401'){
                alert('로그인 후 이용해주세요.');
                location.href='/members/login';
            }else{
                alert(jqXHR.responseText);
            }
        }
    });
}

function prev(){
    var count = $('#count');
    var minus = parseInt(count.val());
    if(minus!=1){
        count.val(--minus);
        calculateTotalPrice();
    }
}

function next(){
    var count = $('#count');
    var stockNumber = $('#stockNumber').val();
    var plus = parseInt(count.val());
    if(plus < stockNumber){
        count.val(++plus);
        calculateTotalPrice();
    }
    else{
        alert("재고수 : "+stockNumber);
    }
}

function priceToString(str) {
    str = String(str);
    return str.replace(/(\d)(?=(?:\d{3})+(?!\d))/g, '$1,');
}

function newComment(itemId, token, header){
    const content = $('#newText').val();
    const contentValid = $.trim(content);
    if(contentValid === ''){
        alert('댓글내용을 입력해주세요.');
        return;
    }
    let paramData = {
        content : content,
        itemId : itemId,
    };
    var param = JSON.stringify(paramData);

    $.ajax({
        url:"/item/newComment",
        type:"post",
        contentType : "application/json",
        dataType:"json",
        data:param,
        beforeSend : function(xhr){
            xhr.setRequestHeader(header, token);
        },
        success: function(data){
            location.reload();
        },
        error : function(jqXHR, status, error){
            if(jqXHR.status == '401'){
                alert('로그인 후 이용해주세요.');
                location.href='/members/login';
            }else{
                alert(jqXHR.responseText);
            }
        }
    });
}

function modifyComment(param, token, header){
    $.ajax({
        url:"/item/modifyComment",
        type:"post",
        contentType : "application/json",
        dataType:"json",
        data:param,
        beforeSend : function(xhr){
            xhr.setRequestHeader(header, token);
        },
        success: function(data){
            location.reload();
        },
        error : function(jqXHR, status, error){
            alert(jqXHR.responseText);
        }
    });
}

function deleteComment(param, token, header){
    $.ajax({
        url:"/item/deleteComment",
        type:"DELETE",
        contentType : "application/json",
        data:param,
        beforeSend : function(xhr){
            xhr.setRequestHeader(header, token);
        },
        success: function(data){
            location.reload();
        },
        error : function(jqXHR, status, error){
            alert(jqXHR.responseText);
        }
    });
}
function pick(){
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    var itemId = $('#itemId').val();

    $.ajax({
        url:"/item/pick/"+itemId,
        type:"PATCH",
        beforeSend : function(xhr){
            xhr.setRequestHeader(header, token);
        },
        success: function(data){
            location.reload();
        },
        error : function(jqXHR, status, error){
            if(jqXHR.status == '401'){
                alert('로그인 후 이용해주세요.');
            }else{
                alert(jqXHR.responseText);
            }
        }
    });
}

function pickCancel(){
    var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        var itemId = $('#itemId').val();

        $.ajax({
            url:"/item/pickCancel/"+itemId,
            type:"PATCH",
            beforeSend : function(xhr){
                xhr.setRequestHeader(header, token);
            },
            success: function(data){
                location.reload();
            },
            error : function(jqXHR, status, error){
                if(jqXHR.status == '401'){
                    alert('로그인 후 이용해주세요.');
                }else{
                    alert(jqXHR.responseText);
                }
            }
        });
}