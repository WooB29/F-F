$(document).ready(function(){
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");

    $("#searchBtn").on("click",function(e){
        e.preventDefault();
        page(0);
    });

    $(".delete").on("click", function(e){
        e.preventDefault();
        var itemId = $(this).data('id');
        itemDelete(itemId, token, header);
    });

    $('.unPick_btn').on('click', function(e){
        e.preventDefault();
        var itemId = $(this).data('id');
        var url = "/admin/item/pick/"+itemId;
        itemPick(itemId, url, token, header);
    })

    $('.pick_btn').on('click', function(e){
        var itemId = $(this).data('id');
        var url = "/admin/item/unPick/"+itemId;
        e.preventDefault();
        itemPick(itemId, url, token, header);
    })
});

function itemPick(itemId, url, token, header){
    $.ajax({
        url: url,
        type : "PATCH",
        dataType : "json",
        cache : false,
        beforeSend : function(xhr){
            xhr.setRequestHeader(header, token);
        },
        success : function(result, status){
            console.log("hi");
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
    })
}

function itemDelete(itemId, token, header){

    var url = "/admin/item/delete/" + itemId;

    $.ajax({
        url : url,
        type : "DELETE",
        beforeSend : function(xhr){
            xhr.setRequestHeader(header, token);
        },
        dataType : "json",
        cache : false,
        success : function(result, status){
            location.href='/admin/items';
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

function page(page){
    var searchDateType = $("#searchDateType").val();
    var searchSellStatus = $("#searchSellStatus").val();
    var searchBigType = $("#searchBigType").val();
    var searchBy = $("#searchBy").val();
    var searchQuery = $("#searchQuery").val();
    location.href="/admin/items/"+page+"?searchDateType="+searchDateType
    +"&searchSellStatus="+searchSellStatus+"&searchBigType="+searchBigType+"&searchBy="+searchBy
    +"&searchQuery="+searchQuery;
}