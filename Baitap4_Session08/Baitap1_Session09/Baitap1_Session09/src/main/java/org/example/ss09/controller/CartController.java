package org.example.ss09.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CartController {

    private static final String CART_SESSION_KEY = "myCart";

    @PostMapping("/add-to-cart")
    public String addToCart(@RequestParam("productId") String productId, HttpSession session) {
        List<String> cart = (List<String>) session.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new ArrayList<>();
        }
        cart.add(productId);
        session.setAttribute(CART_SESSION_KEY, cart);
        return "redirect:/checkout";
    }
    @GetMapping("/checkout")
    public String viewCheckout(HttpSession session, Model model) {
        List<String> cart = (List<String>) session.getAttribute(CART_SESSION_KEY);
        if (cart == null || cart.isEmpty()) {
            model.addAttribute("message", "Giỏ hàng của bạn đang trống!");
        } else {
            model.addAttribute("message", "Bạn có " + cart.size() + " sản phẩm trong giỏ.");
            model.addAttribute("cartItems", cart);
        }
        return "checkout-page";
    }
}
//Phần 1: Phân tích Logic (Trace Code)
//Tại sao giỏ hàng bị "mất trí nhớ"?
//Vấn đề nằm ở việc sử dụng HttpServletRequest để lưu trữ
//dữ liệu giỏ hàng.
//
//Cơ chế của Request: Mỗi khi khách hàng gửi một yêu cầu
//        (click nút, load trang), một đối tượng
//HttpServletRequest mới sẽ được tạo ra và nó chỉ tồn tại
//duy nhất trong vòng đời của request đó. Ngay khi phản hồi (Response)
// được gửi về trình duyệt,
// đối tượng Request này sẽ bị hủy bỏ.
//
//Lỗi do lệnh Redirect: Lệnh return "redirect:/checkout";
//bản chất là gửi một mã phản hồi (thường là HTTP 302)
// yêu cầu trình duyệt thực hiện một Request hoàn toàn mới tới URL /checkout.
//
//Hệ quả: * Tại /add-to-cart: Bạn lưu giỏ hàng vào request.
//
//Lệnh redirect thực hiện: Request cũ (chứa giỏ hàng) bị xóa sổ.
//
//Tại /checkout: Trình duyệt gửi một Request mới tinh. Khi bạn gọi
// request.getAttribute("myCart"), kết quả chắc chắn là null.