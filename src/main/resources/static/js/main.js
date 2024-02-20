var $slides, $slide, currentIdx, slideCount, $prevBtn, $nextBtn, slideWidth, slideMargin, intervalId;

function startSlideshow() {
    intervalId = setInterval(function () {
        moveRight();
    }, 5000);
}

function stopSlideshow() {
    clearInterval(intervalId);
}

function moveSlide(num) {
    $slides.css('left', -(num * (slideWidth + slideMargin)+5) + 'px');
    currentIdx = num;
}

function moveRight() {
    if (currentIdx < slideCount - 3) {
        moveSlide(currentIdx + 1);
    } else {
        moveSlide(0);
    }
}

function moveLeft() {
    if (currentIdx > 0) {
        moveSlide(currentIdx - 1);
    } else {
        moveSlide(slideCount - 3);
    }
}

$(document).ready(function () {
    $('.page_previous').attr('aria-hidden','false');
    $('.page_next').attr('aria-hidden','false');
    $slides = $('.slides');
    $slide = $('.item_li');
    currentIdx = 0;
    slideCount = $slide.length;
    $prevBtn = $('.prev');
    $nextBtn = $('.next');
    slideWidth = 250;
    slideMargin = 30;

    $slides.width((slideWidth + slideMargin) * slideCount - slideMargin);

    $slides.on('mouseenter', function () {
        stopSlideshow();
    });

    $slides.on('mouseleave', function () {
        startSlideshow();
    });

    startSlideshow();

    $('#b1').attr('aria-hidden','false');
    $('#c2').attr('aria-hidden', 'false');

    var page = $('#page');
    var total = $('#total');
    var total_length = $('.banner_itemImg').length;
    var index = 1;
    var index2 = 2;
    var margin = 100;
    var img = 450;
    var smallSize = parseInt(img+margin+(img/2));
    var widthSize = parseInt((img+margin)*total_length);
    $('.banner_div_div_div').css('width',smallSize);
    $('.banner_img').css('width', widthSize);
    page.text(index);
    total.text(" / "+total_length);

    $('#bannerPrev').click(function(){
        if(index > 1){
            index--;
            if(index2==1){
                index2 = 5;
            }
            else{
                index2--;
            }
        }
        else{
            index = total_length;
            index2--;
        }
        bannerChange(index, index2, page);
    });
    $('#bannerNext').click(function(){
        if(index < total_length){
            index++;
            if(index2==total_length){
                index2 = 1;
            }
            else{
                index2++;
            }
        }
        else{
            index = 1;
            index2++;
        }
        bannerChange(index, index2, page);
    });

    window.addEventListener('scroll', scrollListener);

});

function scrollListener() {
    var totalPage = parseInt($('#totalpage').val());
    if ((window.innerHeight + window.scrollY) >= document.body.offsetHeight) {
        loadMoreItems(totalPage);
    }
}

function lastPage(totalPage) {
    var currentPage = parseInt($('#currentPage').val());
    return currentPage >= totalPage;
}

function loadMoreItems(totalPage) {
    var searchQuery = $('#searchQuery').val();
    var nextPage = parseInt($('#currentPage').val()) + 1;

    $.ajax({
        url: '/item/more',
        method: 'GET',
        data: {
            searchQuery: searchQuery,
            page: nextPage
        },
        success: function(response) {
            appendData(response);
            $('#currentPage').val(nextPage);
            if(lastPage(totalPage)){
                window.removeEventListener('scroll', scrollListener);
            }
        },
        error: function(xhr, status, error) {
            console.error(error);
        }
    });
}

function appendData(response){
    response.forEach(function(item) {
        var html = '<div class="item_card">' +
            '<a href="/item/' + item.id + '">' +
            '<img src="' + item.imgUrl + '" alt="' + item.itemNm + '">' +
            '<div class="type_name">' +
            '<label class="text_p">' + item.bigType + '</label>' +
            '</div>' +
            '<div class="item_card_body">' +
            '<h3 class="item_card_title text_smallHeader">' + item.itemNm + '</h3>';
        if (item.discountRate != null) {
            html += '<h3 class="item_card_title text_p">' + item.discountPrice + '원</h3>' +
                '<label class="item_card_text text-danger text_smallP" style="text-decoration: line-through;">' + item.price + '원</label>' +
                '<label class="discounted_price text-danger text_smallP">' + item.discountRate + '%</label>';
        } else {
            html += '<h3 class="item_card_title text_p">' + item.price + '원</h3>';
        }
        html += '</div>' +
            '</a>' +
            '</div>';
        $('.card_box').append(html);
    });
}



function bannerChange(index, index2, page){
    var imgWidth = $('.banner_itemImg').outerWidth(true);
    var newPosition = -(((index - 1) * (imgWidth+100)));
    $('.banner_img').css('left', newPosition);


    $('[aria-hidden="false"]').attr('aria-hidden','true');
    $('#b'+index).attr('aria-hidden','false');

    $('#c'+index2).attr('aria-hidden', 'false');
    page.text(index);
}