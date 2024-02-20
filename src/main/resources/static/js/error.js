$(document).ready(function(){
    var td = $('.li_td');
    td.css('display','none');
    $('.input_check').on('change',function(){
        var num = $(this).data('num');
        if(this.checked){
            $('#liTd'+num).slideDown();
        }
        else{
            $('#liTd'+num).slideUp();
        }
    });

    $('.save_btn').on('click', function(){
        var num = $(this).data('num');
        console.log("num : "+num);
        var errorId = $('#errorId'+num).text();
        var errorStatus = $('#errorStatus'+num).val();
        statusSave(errorId, errorStatus);
    });
});

function statusSave(errorId, errorStatus){
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");

    $.ajax({
        url:"/error/"+errorId+"/change/"+errorStatus,
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