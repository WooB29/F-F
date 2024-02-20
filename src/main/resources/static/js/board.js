$(document).ready(function(){
    var errorMessage = $('#errorMessage').val();
    if(errorMessage !== ''){
        alert(errorMessage);
    }
});

function page(page){
    var searchBy = $("#searchBy").val();
    var searchQuery = $("#searchQuery").val();
    location.href="/community/[[${where}]]/"+page+"?searchBy="+searchBy+"&searchQuery="+searchQuery;
}