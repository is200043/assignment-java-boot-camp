-- product
insert into product
values ('93c14646-add9-4692-8308-d3e37c2eb571',
        'Infinix Note 11S (8GB+128GB) โทรศัพท์ จอใหญ่ 6.95 Full HD 120Hz" แบตฯอึด 5000 mAh มาพร้อม Helio G96 กล้องหน้า 16MP กล้องหลังคมชัดสูงสุด 50MP พร้อมชาร์จเร็ว 33W',
        6199.00, 6299.00, 5, 'Infinix', '{"color": ["MITHRIL GREY", "SYMPHONY CYAN", "HAZE GREEN"], "size": ["128GB"]}',
        '["https://lzd-img-global.slatic.net/g/p/68f750ca3ade11c86e4d150b9b624610.jpg_720x720q80.jpg_.webp", "https://lzd-img-global.slatic.net/g/p/aa40cfa984d0b23e06847b4878dd9683.jpg_720x720q80.jpg_.webp"]',
        3.5, 15, 'smart phones', '', null, 40.00);
insert into product
values ('f06c86b0-3598-4242-bf3e-0b28c3ed0a7e',
        'ซัมซุงฮีโร่ B109H รองรับ 3G/4G Samsung Hero โทรศัพท์ปุ่มกด ทนทาน เมนู/แป้นพิมพ์ไทย', 599.00, 0, 20,
        'Samsung', '{"promotion": ["every 1000 discount 8"]}',
        '["https://lzd-img-global.slatic.net/g/ff/kf/S8a19b5ffe62044499ea76c9e1e453f55P.jpg_720x720q80.jpg_.webp", "https://lzd-img-global.slatic.net/g/ff/kf/S2742f7b80a364262abbdf37d50bc3640d.jpg_720x720q80.jpg_.webp"]',
        5, 30, 'smart phones', '', null, 40.00);
insert into product
values ('43b2c802-dd6e-47df-99e4-511dad7bb9aa',
        'Original Unlocked for iPhone 6s Plus 5.5 inch 2GB RAM 16GB/32GB/64GB/128GB WCDMA 4G LTE（รองรับภาษาไทย',
        4880.00, 18664.00, 10, 'Apple',
        '{"promotion": ["min 4900 discount 200"], "color": ["GREY", "BLACK", "PINK"], "size": ["16GB", "32GB", "64GB", "128GB"]}',
        '["https://lzd-img-global.slatic.net/g/p/df6fdfdaef7927f05cc1abb4ad06148c.jpg_720x720q80.jpg_.webp"]', 4.5, 500,
        'smart phones', '', DATE(NOW()) + INTERVAL 1 MONTH, 40.00);

-- user
insert into user
values ('8d9470e7-979b-4dee-b622-8d40cd55afaf', 'ชลกร ปุนภพถาวร', '565/1 ถนนพุทธมณฑลสาย1 บางด้วน', 'ภาษีเจริญ',
        'กรุงเทพมหานคร', '10160', '0614479979', 'chonlakorn.pun@gmail.com', 'is200043', '');

insert into user_item
values ('8d9470e7-979b-4dee-b622-8d40cd55afaf', '43b2c802-dd6e-47df-99e4-511dad7bb9aa', 2, 4880.00,
        '{"color": "GREY", "size": "64GB"}', 40.00);

insert into user_purchance_history (id, user_id, total_price, total_shipping, total_discount, total_amount, status,
                                    modified_at)
values ('01cb66fa-8f80-46b6-8a85-7b4ba617b89a', '8d9470e7-979b-4dee-b622-8d40cd55afaf', 599.0, 0.0, 40.0, 639.0,
        'Checkout', NOW());

insert into user_purchance_history_detail (user_purchance_history_id, product_id, price, qty, shipping_price, options)
values ('01cb66fa-8f80-46b6-8a85-7b4ba617b89a', 'f06c86b0-3598-4242-bf3e-0b28c3ed0a7e', 599, 1, 40, NULL);