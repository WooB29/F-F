var container = document.getElementById('map');
var options = {
    center: new kakao.maps.LatLng(37.49100, 126.7206),
    level: 3
};
var map = new kakao.maps.Map(container, options);
var geocoder = new kakao.maps.services.Geocoder();

geocoder.addressSearch('부평동 534-48', function(result, status) {
    if (status === kakao.maps.services.Status.OK) {
        var coords = new kakao.maps.LatLng(result[0].y, result[0].x);
        var marker = new kakao.maps.Marker({
            map: map,
            position: coords
        });
        var infowindow = new kakao.maps.InfoWindow({
            content: '<div class="maker"><div class="maker_name text_smallHeader">F & F</div>'+
                        '<span class="maker_address text_p">주소 : 부평동 534-48</span>'+
                    '</div>'
        });
        infowindow.open(map, marker);

        map.setCenter(coords);
    }
});
/*
var position = new kakao.maps.LatLng(37.49100, 126.7206);

var marker = {
    position : position,
}

var container = document.getElementById('map');
var options = {
    center: position,
    level: 3,
    marker: marker
};
var map = new kakao.maps.StaticMap(container, options);
*/