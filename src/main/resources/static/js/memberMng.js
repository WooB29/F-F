$(document).ready(function(){
    $("#searchBtn").on("click",function(e){
        e.preventDefault();
        page(0);
    });

    $(".modify_only").click(function(){
        var row = $(this).closest("tr");
        var email = row.find("td:eq(2)").text();
        var name = row.find("td:eq(1)").text();
        var role = $(this).data('role');
        modify(email,name,role);
    });
});

function save(){
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    const email = $("#modify_email").val();
    const name = $("#modify_name").val();
    const role = $("#modify_select").val();

    let data = {
        "email" : email,
        "name" : name,
        "role" : role
    };
    var param = JSON.stringify(data);
    $.ajax({
        url:"/admin/member/modify",
        type:"POST",
        contentType : "application/json",
        dataType:"json",
        data:param,
        beforeSend : function(xhr){
            xhr.setRequestHeader(header, token);
        },
        success: function(data){
            alert("수정완료");
            location.href='/admin/members';
        },
        error : function(jqXHR, status, error){
            alert(jqXHR.responseText);
        }
    });
}

function modify(email,name,role){
    var modal = $("#modify_modal");
    $("#modify_email").attr('value',email);
    $("#modify_name").attr('value',name);
    $("#modify_select").val(role).prop("selected",true);
    modal.css('display', 'block');
}

function page(page){
    var searchDateType = $("#searchDateType").val();
    var searchRole = $("#searchRole").val();
    var searchWay = $("#searchWay").val();
    var searchName = $("#searchName").val();
    var searchQuery = $("#searchQuery").val();
    location.href="/admin/members/"+page+"?searchDateType="+searchDateType
    +"&searchRole="+searchRole+"&searchWay="+searchWay
    +"&searchName="+searchName+"&searchQuery="+searchQuery;
}

function cancel(){
    var modal = $("#modify_modal");
    modal.css('display', 'none');
}