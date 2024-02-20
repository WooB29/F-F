$(document).ready(function(){
    const where = $("#newBtn").data('where');
    const boardId = $("#newBtn").data('id');
    readURL();
    $("#newBtn").click(function(){
        newComment(boardId, where);
    });
    $(".comment_btn").click(function(){
        const commentId = $(this).data('commentid');
        const dataWhere = $(this).data('where');
        var row = $(this).closest("div");
        var row1 = $(this).closest("li");
        var content = row.find("input:eq(0)").val();
        var addContent = row1.find("textarea:eq(0)").val();
        var childContent = row1.find("input:eq(0)").val();
        actionBtn(commentId, boardId, dataWhere, where, content, addContent, childContent);
    });
    var error = $('#errorMessage').val();
    var errorMessage = $.trim(error);
    if(errorMessage != ''){
        console.log(errorMessage);
        alert(errorMessage);
    }
});

function actionBtn(commentId, boardId, dataWhere, where, content, addContent, childContent){
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    let paramData = {
        commentId : commentId,
        boardId : boardId,
        where : where,
        content : content,
        addContent : addContent,
        childContent : childContent
    };
    var param = JSON.stringify(paramData);

    if(dataWhere==1){
        $.ajax({
            url:"/community/commentModify",
            type:"POST",
            contentType : "application/json",
            data:param,
            dataType:"json",
            beforeSend : function(xhr){
                xhr.setRequestHeader(header, token);
            },
            success: function(data){
                location.reload();
            },
            error : function(jqXHR, status, error){
                alert(jqXHR.status);
                alert(jqXHR.responseText);
            }
        });
    }
    if(dataWhere==2){
        $.ajax({
            url:"/community/commentDelete",
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
                alert(jqXHR.status);
                alert(jqXHR.responseText);
            }
        });
    }
    if(dataWhere==3){
        $.ajax({
            url:"/community/commentChildModify",
            type:"POST",
            contentType : "application/json",
            data:param,
            dataType:"json",
            beforeSend : function(xhr){
                xhr.setRequestHeader(header, token);
            },
            success: function(data){
                location.reload();
            },
            error : function(jqXHR, status, error){
                alert(jqXHR.status);
                alert(jqXHR.responseText);
            }
        });
    }
    if(dataWhere==4){
        $.ajax({
            url:"/community/commentChildDelete",
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
                alert(jqXHR.status);
                alert(jqXHR.responseText);
            }
        });
    }
    if(dataWhere==5){
        $.ajax({
            url:"/community/addComment",
            type:"POST",
            contentType : "application/json",
            data:param,
            dataType:"json",
            beforeSend : function(xhr){
                xhr.setRequestHeader(header, token);
            },
            success: function(data){
                location.reload();
            },
            error : function(jqXHR, status, error){
                if(jqXHR.status == '401'){
                    alert('로그인 후 이용해주세요.');
                }
                else{
                    alert(jqXHR.responseText);
                }
            }
        });
    }
}



function readURL(){
    $(".img_input").on("change", function(){
        var num = $(this).data('num');
        var fileName = $(this).val().split("\\").pop();
        var fileExt = fileName.substring(fileName.lastIndexOf(".")+1);
        fileExt = fileExt.toLowerCase();

        if(fileExt != "jpg" && fileExt != "jpeg" && fileExt != "gif"
        && fileExt != "png" && fileExt != "bmp"){
            alert("이미지 파일만 등록이 가능합니다.");
            $(".preview"+num).attr("src","");
            $(".upload_name.name"+num).val("");
        }
        else{
            var reader = new FileReader();
            reader.onload = function(e) {
                $(".preview"+num).attr("src",e.target.result);
                $(".upload_name.name"+num).val(fileName);
            };
            reader.readAsDataURL(this.files[0]);
        }
    });
}

function newComment(boardId, where){
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    const content = $('#newText').val();
    const contentValid = $.trim(content);

    if(contentValid === ''){
        alert('댓글내용을 입력해주세요.');
        return;
    }
    let paramData = {
        content : content,
        boardId : boardId,
        where : where
    };
    var param = JSON.stringify(paramData);

    $.ajax({
        url:"/community/newComment",
        type:"post",
        contentType : "application/json",
        dataType:"json",
        data:param,
        beforeSend : function(xhr){
            xhr.setRequestHeader(header, token);
        },
        success: function(data){
            //addList(content, data);
            location.reload();
        },
        error : function(jqXHR, status, error){
            if(jqXHR.status == '401'){
                alert('로그인 후 이용해주세요.');
            }
            else{
                alert(jqXHR.responseText);
            }
        }
    });
}

function addList(content, data){
    const div =
    `<div>
        <label th:text="${ct.member.name}" class="label"></label>
        <input type="text" th:value="${ct.content}" th:if="${ct.member.email == who}" class="input">
        <label th:text="${ct.updateTime}" class="label"></label>
        <button th:data-commentid="${ct.id}" class="comment_btn" th:data-where="1" type="button">수정</button>
        <button th:data-commentid="${ct.id}" class="comment_btn" th:data-where="2" type="button">삭제</button>

    </div>`;

    $('#commentList').append(div);
    $('#newText').val('');
}
