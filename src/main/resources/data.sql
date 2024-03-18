-- insert into "USERS" (email, password,is_verified,username) values ('nmkk1234@naver.com','$2y$10$0861E5vbB1j7t0e5VJPRYOjFFUGYOhqRx7P7/QmeNR40quAI/gc6.',true,'test1');
insert into "USERS" (email, password,is_verified,username) values ('1@1.com','$2y$10$0861E5vbB1j7t0e5VJPRYOjFFUGYOhqRx7P7/QmeNR40quAI/gc6.',true,'test');
insert into "USERS" (email, password,username) values ('email2@email.com','$2y$10$0861E5vbB1j7t0e5VJPRYOjFFUGYOhqRx7P7/QmeNR40quAI/gc6.','email');
-- COMMENTS_COUNT  	CREATED_AT  	ID  	READ_COUNT  	USER_ID  	IMAGE  	TITLE  	CONTENT
insert into "POST" (created_at,read_count,user_id,title,content) values ('2024-03-12 20:17:59.878482',0,1,'1','1');
insert into "POST" (created_at,read_count,user_id,title,content) values ('2024-03-12 20:17:59.878483',1,1,'2','2');
insert into "POST" (created_at,read_count,user_id,title,content) values ('2024-03-12 20:17:59.878484',2,1,'3','3');
insert into "POST" (created_at,read_count,user_id,title,content) values ('2024-03-12 20:17:59.878485',3,1,'4','4');
insert into "POST" (created_at,read_count,user_id,title,content) values ('2024-03-12 20:17:59.878486',4,1,'5','5');
insert into "POST" (created_at,read_count,user_id,title,content) values ('2024-03-12 20:17:59.878487',5,1,'6','6');
insert into "POST" (created_at,read_count,user_id,title,content) values ('2024-03-12 20:17:59.878488',6,1,'7','7');
insert into "POST" (created_at,read_count,user_id,title,content) values ('2024-03-12 20:17:59.878489',7,1,'8','8');
insert into "POST" (created_at,read_count,user_id,title,content) values ('2024-03-12 20:17:59.878490',8,1,'9','9');
insert into "POST" (created_at,read_count,user_id,title,content) values ('2024-03-12 20:17:59.878491',9,1,'10','10');
insert into "NOTIFICATION" (is_read,created_at,reciever,message) values (false,'2024-03-15 20:17:59.878491',1,'test');

-- insert into "ROLES" (ID, name) values (1, 'ADMIN');
-- insert into "USERS_ROLES" (roles_id, user_info_id) values (1, 1);