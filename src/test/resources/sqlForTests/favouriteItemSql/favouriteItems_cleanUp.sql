delete from favourite_product where id > 0;

alter sequence favourite_product_id_seq restart with 1;