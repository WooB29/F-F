$(document).ready(function(){
    const data = $('#flowerInfo').val();
    inputData(data);

    $('#detailBtn').on('click', function(){
        $('.today_div').css('display','none');
        $('.flower_info').css('display','block');
    });
    $('#simpleBtn').on('click',function(){
        $('.today_div').css('display','block');
        $('.flower_info').css('display','none');
    })
});

function inputData(data){
    console.log(data);
    const parser = new DOMParser();
    const xmlDoc = parser.parseFromString(data, "text/xml");

    const dataNo = xmlDoc.querySelector("dataNo").textContent;
    const repcategory = xmlDoc.querySelector("repcategory").textContent;
    const flowNm = xmlDoc.querySelector("flowNm").textContent;
    const flowLang = xmlDoc.querySelector("flowLang").textContent;
    const fContent = xmlDoc.querySelector("fContent").textContent;
    const fUse = xmlDoc.querySelector("fUse").textContent;
    const fGrow = xmlDoc.querySelector("fGrow").textContent;
    const fType = xmlDoc.querySelector("fType").textContent;
    const imgUrl1 = xmlDoc.querySelector("imgUrl1").textContent;
    const imgUrl2 = xmlDoc.querySelector("imgUrl2").textContent;
    const imgUrl3 = xmlDoc.querySelector("imgUrl3").textContent;
    const fMonth = xmlDoc.querySelector("fMonth").textContent;
    const fDay = xmlDoc.querySelector("fDay").textContent;

    const inputDataNo = $('#dataNo');
    inputDataNo.val(dataNo);

    $(".today_div h2").text(repcategory);
    $(".today_div img").attr("src", imgUrl1);
    $(".today_div h3").text(flowNm);
    $(".today_div p").text(flowLang);

    $(".img_div").html("");
    const bigImg = $("<img>").attr("id","bigImg").attr("src",imgUrl1).attr("alt","확대사진");
    $(".img_div").append(bigImg);
    const imgDiv = $("<div>").attr("class","images");
    $(".img_div").append(imgDiv);


    for (let i = 1; i <= 3; i++) {
        const img = $("<img>").attr("src", xmlDoc.querySelector(`imgUrl${i}`).textContent).attr("alt", `${i}번 이미지`).attr("onclick","changeImg(this)");
        $(".images").append(img);
    }

    $(".flower_info label:nth-child(1)").text(fMonth + "월");
    $(".flower_info label:nth-child(2)").text(fDay + "일");
    $(".flower_info div:nth-child(2) label:nth-child(1)").text(flowNm);
    $(".flower_info div:nth-child(2) label:nth-child(2)").text(flowLang);
    $(".flower_info div:nth-child(2) p").html(textEnter(fContent));

    $(".flower_habitat h4").text("# 꽃 자생처");
    $(".flower_habitat p").html(textEnter(fUse));

    $(".flower_growing h4").text("# 꽃 기르는법");
    $(".flower_growing p").html(textEnter(fGrow));

    $(".flower_purpose h4").text("# 꽃 용도");
    $(".flower_purpose p").html(textEnter(fType));
}

function textEnter(text) {
    const slice = text.split('.');
    let htmlString = '';
    slice.forEach((a, index) => {
        const trimSlice = a.trim();
        if(index < slice.length-1){
            htmlString += trimSlice+'.<br>';
        }
        else{
            htmlString += trimSlice;
        }
    });
    return htmlString;
}


function changeImg(e){
    var src = $(e).attr("src");
    $("#bigImg").attr("src",src);
}