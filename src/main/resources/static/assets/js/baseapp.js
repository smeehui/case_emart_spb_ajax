const PORT = 8096;

class AppBase {

    static DOMAIN_JSON_SERVER = "http://localhost:3300";
    static DOMAIN_SPB_API = `http://localhost:${PORT}/api`

    static BASE_URL_CLOUD_IMAGE = "https://res.cloudinary.com/smeenguyen/image/upload";
    static BASE_SCALE_IMAGE = "c_limit,w_60,h_60,q_100";
    static BASE_PROMOTE_PRODUCT_SCALE_IMAGE = "c_limit,w_242,h_302,q_100";
    static BASE_SHOP_PRODUCT_SCALE_IMAGE = "c_limit,w_195,h_243,q_100";
    static BASE_CART_PRODUCT_SCALE_IMAGE = "c_limit,w_65,h_65,q_100";
    static function

    static errorNotify(jqXHR) {
        let error;
        switch (jqXHR.status) {
            case 400:
                error = jqXHR.responseJSON;
                AppBase.SweetAlert.showErrorAlert(error.message);
                break;
            case 404:
                error = jqXHR.responseJSON;
                AppBase.SweetAlert.showErrorAlert(error.message);
                break;
            case 401:
                SweetAlert.showError401();
                break;
            case 403:
                SweetAlert.showError403();
                break;
            default:
                AppBase.SweetAlert.showErrorAlert("Something went wrong, please try again later!");
        }
    }

    static SweetAlert = class {
        static showDeleteConfirmDialog() {
            return Swal.fire({
                title: "Are you sure?",
                text: "You won't be able to revert this!",
                icon: "warning",
                showCancelButton: true,
                confirmButtonColor: "#3085d6",
                cancelButtonColor: "#d33",
                confirmButtonText: "Yes, delete it!",
            });
        }

        static showSuccessAlert(t) {
            Swal.fire({
                icon: "success",
                title: t,
                position: "top-end",
                showConfirmButton: false,
                timer: 2000,
            });
        }

        static showErrorAlert(t) {
            Swal.fire({
                icon: "error",
                title: "Warning",
                text: t,
            });
        }

        static showError401() {
            Swal.fire({
                icon: "error",
                title: "Access Denied",
                text: "Invalid credentials!",
            });
        }

        static showError403() {
            Swal.fire({
                icon: "error",
                title: "Access Denied",
                text: "You are not authorized to perform this function!",
            });
        }
    };
}

class User {
    constructor(fullName, phone, address, email, username, password, roles) {
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
        this.email = email;
        this.username = username;
        this.password = password;
        this.roles = roles;

    }
}
