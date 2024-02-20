$(document).ready(function(){
    readURL();
});

function bigImg(img){
    var modal = $('#myModal');
    var modalImg = $('#img01');
    modal.css("display","block");
    modalImg.attr("src", img.src);
    var span = document.getElementsByClassName("close")[0];
    span.onclick = function() {
        modal.css("display","none");
    }
}

function identifyFlower() {
    var img = $('#imageFileInput').val();
    if($.trim(img) == ''){
        alert('이미지를 삽입해주세요.');
        return;
    }

    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    var formData = new FormData();
    formData.append('imageFile', document.getElementById('imageFileInput').files[0]);
    startSpinner();
    $.ajax({
        type: 'POST',
        url: '/api/identify-flower',
        data: formData,
        contentType: false,
        processData: false,
        beforeSend : function(xhr){
            xhr.setRequestHeader(header, token);
        },
        success: function(result) {
            var resultObj = JSON.parse(result);
            if (resultObj && resultObj.result && resultObj.result.classification && resultObj.result.classification.suggestions) {
                var suggestions = resultObj.result.classification.suggestions;
                var tableHtml = '';
                suggestions.forEach(function(suggestion) {
                    tableHtml += '<tr class="text_smallHeader">';
                    tableHtml += '<td class="img_name">' + suggestion.name + '</td>';
                    tableHtml += '<td class="img_td">';
                    suggestion.similar_images.forEach(function(image) {
                        tableHtml += '<div>'
                        tableHtml += '<img src="' + image.url + '" width="100" height="100" class="img_img" onclick="bigImg(this)">';
                        tableHtml += '<label>' + (image.similarity * 100).toFixed(1) + '%' + '</label>';
                        tableHtml += '</div>'
                    });
                    tableHtml += '</td>';
                    tableHtml += '<td class="img_btn">';
                    tableHtml += '<button class="search_btn text_button" onclick="showSearch(\'' + suggestion.name + '\')">구글 검색</button>';
                    tableHtml += '</td>';
                    tableHtml += '</tr>';
                });
                $('#resultBody').html(tableHtml);
                $('#resultMessage').hide();
            } else {
                $('#resultBody').empty();
                $('#resultMessage').show();
            }
            stopSpinner();
        },
        error: function(xhr, status, error) {
            stopSpinner();
            console.error('Error:', error);
        }
    });
}

function readURL(){
    $("#imageFileInput").on("change", function(){
        var fileName = $(this).val().split("\\").pop();
        var fileExt = fileName.substring(fileName.lastIndexOf(".")+1);
        fileExt = fileExt.toLowerCase();

        if(fileExt != "jpg" && fileExt != "jpeg" && fileExt != "gif"
        && fileExt != "png" && fileExt != "bmp"){
            alert("이미지 파일만 등록이 가능합니다.");
            $("#uploadedImage").attr("src","");
        }
        else{
            var reader = new FileReader();
            reader.onload = function(e) {
                $("#uploadedImage").attr("src",e.target.result);
            };
            reader.readAsDataURL(this.files[0]);
        }
    });
}

function showSearch(name) {
    var searchUrl = "https://www.google.com/search?q=" + encodeURIComponent(name);
    window.open(searchUrl, '_blank');
}