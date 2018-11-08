# Spring Boot, MySQL, Spring Security, JWT, JPA, Rest API

Build Restful CRUD API for a blog using Spring Boot, Mysql, JPA and Hibernate.

## Steps to Setup

**1. Clone the application**

```bash
git clone https://github.com/coma123/Spring-Boot-Blog-REST-API.git
```

**2. Create Mysql database**
```bash
create database blogapi
```
+ run `src/main/resources/blogapi.sql`

```bash
INSERT INTO blogapi.roles(name) VALUES('ROLE_ADMIN'), ('ROLE_USER')
```

**3. Change mysql username and password as per your installation**

+ open `src/main/resources/application.properties`
+ change `spring.datasource.username` and `spring.datasource.password` as per your mysql installation

**4. Run the app using maven**

```bash
mvn spring-boot:run
```
The app will start running at <http://localhost:8080>

## Explore Rest APIs

The app defines following CRUD APIs.

### Auth

| Method | Url | Decription | Sample Valid Request Body | 
| ------ | --- | ---------- | --------------------------- |
| POST   | /api/auth/signup | Sign up | [JSON](#signup) |
| POST   | /api/auth/signin | Log in | [JSON](#signin) |

### Users

| Method | Url | Description | Sample Valid Request Body |
| ------ | --- | ---------- | --------------------------- |
| GET    | /api/users/me | Get logged in user profile | |
| GET    | /api/users/{username}/profile | Get user profile by username | |
| GET    | /api/users/{username}/posts | Get posts created by user | |
| GET    | /api/users/{username}/albums | Get albums created by user | |
| GET    | /api/users/checkUsernameAvailability | Check if username is available to register | |
| GET    | /api/users/checkEmailAvailability | Check if email is available to register | |
| POST   | /api/users | Add user (Only for admins) | [JSON](#usercreate) |
| PUT    | /api/users/{username} | Update user (For logged in user) | [JSON](#userupdate) |
| DELETE | /api/users/{username} | Delete user (For logged in user or admin) | |
| PUT    | /api/users/{username}/giveAdmin | Give admin role to user (only for admins) | |
| PUT    | /api/users/{username}/TakeAdmin | Take admin role from user (only for admins) | |
| PUT    | /api/users/setOrUpdateInfo | Update user profile (For logged in user) | [JSON](#userinfoupdate) |

### Posts

| Method | Url | Description |
| ------ | --- | ---------- |
| GET    | /api/posts | Get all posts |
| GET    | /api/posts/{id} | Get post by id |
| POST   | /api/posts | Create new post (By logged in user) |
| PUT    | /api/posts/{id} | Update post (If post belongs to logged in user or logged in user is admin) |
| DELETE | /api/posts/{id} | Delete post (If post belongs to logged in user or logged in user is admin) |

### Comments

| Method | Url | Description |
| ------ | --- | ---------- |
| GET    | /api/posts/{postId}/comments | Get all comments which belongs to post with id = postId |
| GET    | /api/posts/{postId}/comments/{id} | Get comment by id if it belongs to post with id = postId |
| POST   | /api/posts/{postId}/comments | Create new comment for post with id = postId (By logged in user) |
| PUT    | /api/posts/{postId}/comments/{id} | Update comment by id if it belongs to post with id = postId (If comment belongs to logged in user) |
| DELETE | /api/posts/{postId}/comments/{id} | Delete comment by id if it belongs to post with id = postId (If comment belongs to logged in user) |

### Albums

| Method | Url | Description |
| ------ | --- | ---------- |
| GET    | /api/albums | Get all albums |
| GET    | /api/albums/{id} | Get album by id |
| POST   | /api/albums | Create new album (By logged in user) |
| PUT    | /api/albums/{id} | Update album (If album belongs to logged in user) |
| DELETE | /api/albums/{id} | Delete album (If album belongs to logged in user) |
| GET    | /api/albums/{id}/photos | Get all photos which belongs to album with id = id |

### Photos

| Method | Url | Description |
| ------ | --- | ---------- |
| GET    | /api/photos | Get all photos |
| GET    | /api/photos/{id} | Get photo by id |
| POST   | /api/photos | Create new photo (By logged in user) |
| PUT    | /api/photos/{id} | Update photo (If photo belongs to logged in user) |
| DELETE | /api/photos/{id} | Delete photo (If photo belongs to logged in user) |

### Todos

| Method | Url | Description |
| ------ | --- | ---------- |
| GET    | /api/todos | Get all todos which belongs to logged in user |
| GET    | /api/todos/{id} | Get todo by id (If todo belongs to logged in user) |
| POST   | /api/todos | Create new todo (By logged in user) |
| PUT    | /api/todos/{id} | Update todo (If todo belongs to logged in user) |
| DELETE | /api/todos/{id} | Delete todo (If todo belongs to logged in user) |
| PUT    | /api/todos/{id}/complete | Mark todo as complete (If todo belongs to logged in user) |
| PUT    | /api/todos/{id}/unComplete | Mark todo as uncomplete (If todo belongs to logged in user) |

Test them using postman or any other rest client.

## Sample Valid JSON Request Bodys

##### <a id="signup">api/auth/signup</a>
```json
{
	"firstName": "Leanne",
	"lastName": "Graham",
	"username": "leanne",
	"password": "password",
	"email": "leanne.graham@gmail.com"
}
```

##### <a id="signin">/api/auth/signin</a>
```json
{
	"usernameOrEmail": "leanne",
	"password": "password"
}
```

##### <a id="usercreate">/api/users</a>
```json
{
	"firstName": "Ervin",
	"lastName": "Howell",
	"username": "ervin",
	"password": "password",
	"email": "ervin.howell@gmail.com",
	"address": {
		"street": "Victor Plains",
		"suite": "Suite 879",
		"city": "Wisokyburgh",
		"zipcode": "90566-7771",
		"geo": {
			"lat": "-43.9509",
			"lng": "-34.4618"
		}
	},
	"phone": "010-692-6593 x09125",
	"website": "http://erwinhowell.com",
	"company": {
		"name": "Deckow-Crist",
		"catchPhrase": "Proactive didactic contingency",
		"bs": "synergize scalable supply-chains"
	}
}
```

##### <a id="userupdate">/api/users/{username}</a>
```json
{
	"firstName": "Ervin",
	"lastName": "Howell",
	"username": "ervin",
	"password": "updatedpassword",
	"email": "ervin.howell@gmail.com",
	"address": {
		"street": "Victor Plains",
		"suite": "Suite 879",
		"city": "Wisokyburgh",
		"zipcode": "90566-7771",
		"geo": {
			"lat": "-43.9509",
			"lng": "-34.4618"
		}
	},
	"phone": "010-692-6593 x09125",
	"website": "http://erwinhowell.com",
	"company": {
		"name": "Deckow-Crist",
		"catchPhrase": "Proactive didactic contingency",
		"bs": "synergize scalable supply-chains"
	}
}
```

##### <a id="userinfoupdate">/api/users/setOrUpdateInfo</a>
```json
{
	"street": "Douglas Extension",
	"suite": "Suite 847",
	"city": "McKenziehaven",
	"zipcode": "59590-4157",
	"companyName": "Romaguera-Jacobson",
	"catchPhrase": "Face to face bifurcated interface",
	"bs": "e-enable strategic applications",
	"website": "http://ramiro.info",
	"phone": "1-463-123-4447",
	"lat": "-68.6102",
	"lng": "-47.0653"
}
```
