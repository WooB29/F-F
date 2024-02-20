function deletePick(){
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    var itemId = $(event.target).data('id');

    $.ajax({
        url:"/item/pickCancel/"+itemId,
        type:"PATCH",
        beforeSend : function(xhr){
            xhr.setRequestHeader(header, token);
        },
        success: function(data){
            location.reload();
            //$(".heart_img[data-id='" + itemId + "']").attr("src","/img/noHeart.gif");
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

