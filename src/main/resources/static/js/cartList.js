$(document).ready(function(){
    $("input[name=cartChkBox]").change(function(){
        getOrderTotalPrice();
    });

    $('.prev').click(function(){
        const row = $(this).closest("div");
        const input = row.find("input:eq(1)");
        prev(input);
    });

    $('.next').click(function(){
        const row = $(this).closest("div");
        const input = row.find("input:eq(1)");
        next(input);
    });

});


function prev(count){
    var minus = parseInt(count.val());
    if(minus!=1){
        count.val(--minus);
        changeCount(count.get(0));
    }
}

function next(count){
    var plus = parseInt(count.val());
    count.val(++plus);
    changeCount(count.get(0));
}


function getOrderTotalPrice(){
    var orderTotalPrice = 0;
    $("input[name=cartChkBox]:checked").each(function(){
        var cartItemId = $(this).val();

        var price = $("#price_" + cartItemId).attr("data-price");
        var count = $("#count_" + cartItemId).val();
        orderTotalPrice += price * count;
    });

    $("#orderTotalPrice").html(priceToString(orderTotalPrice)+'원');
}

function changeCount(obj){
    var count = obj.value;
    var cartItemId = obj.id.split('_')[1];
    var price = $("#price_"+cartItemId).data("price");
    var totalPrice = count * price;
    $("#totalPrice_" + cartItemId).html(priceToString(totalPrice)+"원");
    getOrderTotalPrice();
    updateCartItemCount(cartItemId, count);
}
function checkAll(){
    if($("#checkall").prop("checked")){
        $("input[name=cartChkBox]").prop("checked", true);
    }
    else{
        $("input[name=cartChkBox]").prop("checked", false);
    }
    getOrderTotalPrice();
}

function updateCartItemCount(cartItemId, count){
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");

    var url = "/cartItem/" + cartItemId + "?count=" + count;

    $.ajax({
        url : url,
        type : "PATCH",
        beforeSend : function(xhr){
            xhr.setRequestHeader(header, token);
        },
        dataType : "json",
        cache : false,
        success : function(result, status){
            console.log("cartItem count update success");
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

function deleteCartItem(obj){
    var cartItemId = obj.dataset.id;
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");

    var url = "/cartItem/" + cartItemId;

    $.ajax({
        url : url,
        type : "DELETE",
        beforeSend : function(xhr){
            xhr.setRequestHeader(header, token);
        },
        dataType : "json",
        cache : false,
        success : function(result, status){
            location.href='/cart';
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

function uniqueNumber() {
    var timestamp = new Date().getTime();
    var randomNumber = Math.floor(Math.random() * 10000);
    return 'ORD' + timestamp + '-' + randomNumber;
}

function orders(){
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    var uid = uniqueNumber();

    var memberEmail = $('#memberEmail').val();
    var check = $.trim(memberEmail);
    if(check == ''){
        alert("로그인 후 이용해주세요.");
        return;
    }

    var checkedItems = $("input[name=cartChkBox]:checked");
    if (checkedItems.length === 0) {
        alert("주문할 상품을 선택해주세요.");
        return;
    }
    var dataList = new Array();
    var paramData = new Object();

    var outCheck = false;

    checkedItems.each(function () {
        var cartItemId = $(this).val();
        var stockNumber = parseInt($("#stockNumber_"+cartItemId).val(), 10);
        var count = parseInt($("#count_" + cartItemId).val(), 10);
        var itemNm = $("#itemNm_" + cartItemId).text();
        if (count > stockNumber) {
            alert("재고 수량 초과 : " + stockNumber + " (" + itemNm + ")");
            outCheck = true;
            return false;
        }
        var data = new Object();
        data["cartItemId"] = cartItemId;
        dataList.push(data);
    });

    if(outCheck){
        return;
    }

    var totalText = $('#orderTotalPrice').text();
    var total = parseInt(totalText.replace('원','').replace(',', ''));

    var IMP = window.IMP;
    IMP.init("imp63560331");
    IMP.request_pay({
        pg: "kakaopay.TC0ONETIME",
        pay_method: "card",
        merchant_uid: uid,
        name: "장바구니",
        amount: total,
        buyer_email: memberEmail,
    }, function (rsp) {
        if(rsp.success){
            alert("결제 완료");
            var url = "/cart/orders";

            paramData['cartOrderDtoList'] = dataList;
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
                    location.href='/orders';
                },
                error : function(jqXHR, status, error){
                    var errorData = {
                        email : memberEmail,
                        code : jqXHR.status,
                        content : jqXHR.responseText,
                        location : "cart",
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

function priceToString(str) {
    str = String(str);
    return str.replace(/(\d)(?=(?:\d{3})+(?!\d))/g, '$1,');
}