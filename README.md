## SWP PROJECT
1. KHO LỆNH GITHUB

📌. ** Cách kiểm tra đóng góp cá nhân trên GitHub`:**
 ```sh
   git shortlog -s -n
  ```

### Mô hình nhánh hợp lý:
- **`main`** → Chỉ merge code đã kiểm tra xong.
- **`develop`** → Nhánh chính để phát triển (mọi tính năng sẽ merge vào đây trước).
- **Nhánh cá nhân (`feature/xxx`)** → Mỗi thành viên tự code trên nhánh riêng.

---

### Cách làm việc cụ thể:

#### (1) Mỗi tính năng sẽ tạo một nhánh riêng
Ví dụ: Bạn đang làm chức năng **login**, tạo nhánh như sau:
```sh
git checkout develop 
git checkout -b feature/login
```

#### (2) Làm xong thì commit & push
```sh
git add .
git commit -m "Thêm chức năng login"
git push origin feature/login
```

#### (3) Merge code vào `develop` trước
Sau khi hoàn tất tính năng, merge vào **develop**:
```sh
git checkout develop
git pull origin develop
git merge feature/login
git push origin develop
```

#### (4) Chỉ merge `develop` vào `main` khi code đã ổn định
```sh
git checkout main
git merge develop
git push origin main
```

---

### 🛠 Cách xóa nhánh Git đúng cách
#### (1) Xóa nhánh local (trên máy)

```sh
git branch -d feature/abc  # Xóa nhánh đã merge
git branch -D feature/abc  # Xóa nhánh chưa merge (nếu chắc chắn không cần nữa)
```

#### (2) Xóa nhánh trên remote (GitHub)
git push origin --delete feature/abc


### Một số lệnh hữu ích khác:

**Kiểm tra trạng thái của Git:**
```sh
git status
```

**Xem danh sách nhánh(Kiểm tra xem bạn đang ở nhánh nào):**
```sh
git branch
```

**Xóa nhánh sau khi merge (local & remote):**
```sh
git branch -d feature/login  # Xóa nhánh local
git push origin --delete feature/login  # Xóa nhánh trên remote
```

**Lấy code mới nhất từ remote:**
```sh
git pull origin main  # Hoặc develop
```

---
### Nếu commit cũ bị lỗi, làm sao cập nhật lại?

1. **Sửa lỗi trong code**, sau đó commit lại:
   ```sh
   git add .
   git commit -m "Sửa lỗi login"
   ```

2. **Nếu chưa push, chỉnh sửa commit cuối cùng:**
   ```sh
   git commit --amend -m "Cập nhật lại login"
   ```

3. **Nếu đã push, sửa lại và force push:**
   ```sh
   git push origin feature/login --force
   ```

4. **Nếu muốn revert commit lỗi (đã push):**
   ```sh
   git revert <commit_id>
   git push origin feature/login
   ```

5. **Nếu muốn reset về trạng thái trước commit lỗi:**
   ```sh
   git reset --hard <commit_id>
   git push origin feature/login --force
   ```

6. **Nếu commit lỗi đã merge vào `develop` hoặc `main`:**
   ```sh
   git revert <commit_id>  # Tạo commit mới để đảo ngược commit lỗi
   git push origin develop  # Hoặc main
   ```


