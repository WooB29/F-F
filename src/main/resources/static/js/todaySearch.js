$(document).ready(function(){
    updateSearchForm();
    $("#search_data").on('change',function(){
        updateSearchForm();
    });

    $("#search_button").on('click',function(){
        startSpinner();
        searchData();
    });
});

function searchData(){
    var selectedOption = $("#search_data").val();
    var todaySearchDto = {
        "month": '',
        "day": '',
        "searchType": '',
        "searchWord": '',
        "pageNo":'1',
    };

    if (selectedOption === "date") {
        var selectedDate = $("#daySelect").val();
        var selectedMonth = $("#monthSelect").val();
        todaySearchDto.month = selectedMonth;
        todaySearchDto.day = selectedDate;
    }
    if (selectedOption === "name") {
        var nameValue = $("#select_name input").val();
        if($.trim(nameValue) == ''){
            alert("검색어를 입력해주세요.");
            return;
        }
        todaySearchDto.searchType = '1';
        todaySearchDto.searchWord = nameValue;
    }
    if (selectedOption === "language") {
        var languageValue = $("#select_language input").val();
        if($.trim(languageValue) == ''){
            alert("검색어를 입력해주세요.");
            return;
        }
        todaySearchDto.searchType = '4';
        todaySearchDto.searchWord = languageValue;
    }
    $("#pageData").val(JSON.stringify(todaySearchDto));
    dataSend(todaySearchDto, "1");
}

function nextSearch(page){
    startSpinner();
    var todaySearchDtoString = $("#pageData").val();
    var todaySearchDto = JSON.parse(todaySearchDtoString);
    todaySearchDto.pageNo = page;
    dataSend(todaySearchDto);
}

function dataSend(todaySearchDto){
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $.ajax({
        url: "/api/todaySearch",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(todaySearchDto),
        beforeSend : function(xhr){
            xhr.setRequestHeader(header, token);
        },
        success: function(response) {
            inputData(response);
            stopSpinner();
        },
        error: function(xhr, status, error) {
            stopSpinner();
            alert(xhr.responseText);
        }
    });
}

function inputData(response){
    var searchDataList = $("#searchDataList");
    searchDataList.empty();
    var repcategory = $(response).find('repcategory').text();
    var h2 = $("<h2>").text(repcategory);
    searchDataList.append(h2);

    var resultCnt = $(response).find('resultCnt').text();

    var data = $(response).find('result');

    for (var i = 0; i < data.length; i += 2) {
        var div = $("<div>").addClass("list_items");

        var item1 = data[i];
        var a1 = createItemDiv(item1);
        div.append(a1);

        if (i + 1 < data.length) {
            var item2 = data[i + 1];
            var a2 = createItemDiv(item2);
            div.append(a2);
        }
        searchDataList.append(div);
    }

    var pageNo = $(response).find('pageNo').text();
    var totalPages = Math.ceil(parseInt(resultCnt) / 10);
    var button_div = $("<div>").addClass("button_div");
    for (var page = 1; page <= totalPages; page++) {
        var pageButton = $("<button>").addClass('page_button text_button').text(page);
        if(pageNo == page){
            pageButton.attr('disabled', 'disabled');
        }
        button_div.append(pageButton);

        pageButton.on('click',function(){
            var selectedPage = $(this).text();
            nextSearch(selectedPage);
        });
    }
    searchDataList.append(button_div);

    function createItemDiv(item) {
        var fMonth = $(item).find('fMonth').text();
        var fDay = $(item).find('fDay').text();
        var a = $("<a>").addClass("items_div").attr("href", "/api/today-flower/" + fMonth + "/" + fDay);

        var div1 = $("<div>");
        var p1 = $("<p>").append("<span>" + fMonth + " 월&nbsp;</span>").append("<span>" + fDay + " 일</span>");
        var p2 = $("<p>").text($(item).find('flowNm').text());
        var flowLang = $(item).find('flowLang').text();
        var p3 = $("<p>").html(textEnter(flowLang));

        div1.append(p1).append(p2);
        a.append(div1).append(p3);
        return a;
    }
}

function updateSearchForm() {
    var selectedOption = $("#search_data").val();
    var searchForm = $("#search_form");

    if(selectedOption == 'date'){
        searchForm.html("");
        searchForm.append(createDateSearchForm());
        updateDaySelect();
    }
    else if(selectedOption == 'name'){
        searchForm.html("");
        searchForm.append(createNameSearchForm());
    }
    else if(selectedOption == 'language'){
        searchForm.html("");
        searchForm.append(createLanguageSearchForm());
    }
    else{
        searchForm.empty();
    }
}

function createDateSearchForm() {
    var title = $('#h2Title');
    title.text("날짜를 선택해주세요.");
    var selectDateDiv = $("<div>").attr("id", "select_date");
    var monthSelect = $("<select>").attr("id", "monthSelect").attr("class","flower_date").on("change", updateDaySelect);
    for (var i = 1; i <= 12; i++) {
        monthSelect.append($("<option>").val(i).text(i + "월"));
    }
    var daySelect = $("<select>").attr("id", "daySelect").attr("class","flower_date");
    selectDateDiv.append(monthSelect).append(daySelect);

    return selectDateDiv;
}

function createNameSearchForm() {
    var title = $('#h2Title');
    title.text("이름을 입력해주세요.");
    var selectNameDiv = $("<div>").attr("id", "select_name");
    selectNameDiv.append($("<input>").attr("type", "text"));
    return selectNameDiv;
}

function createLanguageSearchForm() {
    var title = $('#h2Title');
    title.text("꽃말을 입력해주세요.");
    var selectLanguageDiv = $("<div>").attr("id", "select_language");
    selectLanguageDiv.append($("<input>").attr("type", "text"));
    return selectLanguageDiv;
}

function updateDaySelect() {
    var selectedMonth = parseInt($("#monthSelect").val());
    var daysInMonth = new Date(new Date().getFullYear(), selectedMonth, 0).getDate();
    var daySelect = $("#daySelect");

    daySelect.empty();

    for (var i = 1; i <= daysInMonth; i++) {
        daySelect.append($("<option></option>").text(i + "일").val(i));
    }
}

function textEnter(text) {
    const slice = text.split(',');
    let htmlString = '';
    slice.forEach((a, index) => {
        const trimSlice = a.trim();
        if(index < slice.length-1){
            htmlString += trimSlice+',<br>';
        }
        else{
            htmlString += trimSlice;
        }
    });
    return htmlString;
}

