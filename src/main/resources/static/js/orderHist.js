function cancelOrder(orderId){
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    var num = $(event.target).data('num');
    var url = "/order/" + orderId +"/cancel/"+num;
    var paramData = {
        orderId : orderId,
        num : num,
    };
    console.log("num : "+num);
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
            location.reload();
        },
        error : function(jqXHR, status, error){
            if(jqXHR.status == '401'){
                alert("로그인 후 이용해주세요.");
                location.href='/members/login';
            }else{
                alert(jqXHR.responseText);
            }
        }
    });
}