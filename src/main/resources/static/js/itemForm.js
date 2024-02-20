$(document).ready(function(){
    selectChange();
    $('#bigType').change(function(){
        selectChange();
    });

    readURL();

    $("#discount_checkbox").click(function() {
        discount_check();
    });
    changeDiscount_result();

    $("#discount_input, #item_price").on("input", function() {
        changeDiscount_result();
    });
});

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


function selectChange(data){
    var bigType = $('#bigType').val();
    const smallType = $('#smallType');

    var one = $('#one');
    var two = $('#two');

    if (bigType == 'FLOWER'){
        one.text('꽃 다발');
        one.attr('value', 'SINGLE');
        two.text('꽃 바구니');
        two.attr('value','MULTI');
    }
    if (bigType == 'PLANTS'){
        one.text('개업 식물');
        one.attr('value','OPEN');
        two.text('관엽 식물');
        two.attr('value','FOLIAGE');
    }
    if (bigType == 'RAN'){
        one.text('동양란');
        one.attr('value','EAST');
        two.text('서양란');
        two.attr('value','WEST');
    }
    if (bigType == 'FLOWERY'){
        one.text('축하 화한');
        one.attr('value','GOOD');
        two.text('근조 화한');
        two.attr('value','WORST');
    }
}

function discount_check() {
    var discount_input = $("#discount_input");
    if ($("#discount_checkbox").prop("checked")) {
        discount_input.prop("disabled", false);
    } else {
        discount_input.val(null).prop("disabled", true);
        $("#discount_result").val("");
    }
}

function changeDiscount_result() {
    var item_price = parseFloat($("#item_price").val());
    var discount_input = $("#discount_input");
    var discount_resultInput = $("#discount_result");
    var discountRate = parseFloat(discount_input.val());


    if (!isNaN(item_price) && !discount_input.prop("disabled")) {
        if (!isNaN(discountRate)) {
            var discount_result = Math.floor((item_price - (item_price * discountRate / 100))/100)*100;
            discount_resultInput.val(discount_result);
        }
        else{
            discount_resultInput.val('');
        }
    }
}