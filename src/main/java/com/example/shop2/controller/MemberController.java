package com.example.shop2.controller;

import com.example.shop2.dto.MemberJoinForm;
import com.example.shop2.security.PrincipalDetails;
import com.example.shop2.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 회원가입, 로그인 화면, 마이페이지 요청 처리
 */
@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /**
     * 회원가입 화면 보여주기 (GET /join)
     */
    @GetMapping("/join")
    public String joinForm(Model model) {
        // 빈 신청서를 화면에 전달 (화면의 th:object="${joinForm}"와 연결돼요)
        model.addAttribute("joinForm", new MemberJoinForm());
        return "member/join";
    }

    /**
     * 회원가입 처리 (POST /join)
     * @Valid → MemberJoinForm에 적어둔 검사 규칙을 실행
     * BindingResult → 검사에서 걸린 오류들이 담기는 곳 (반드시 @Valid 바로 뒤에 있어야 해요)
     */
    @PostMapping("/join")
    public String join(@Valid @ModelAttribute("joinForm") MemberJoinForm form,
                       BindingResult bindingResult,
                       RedirectAttributes redirectAttributes) {

        // 1. 비밀번호와 비밀번호 확인이 같은지 검사
        if (form.getPassword() != null && !form.getPassword().equals(form.getPasswordConfirm())) {
            bindingResult.rejectValue("passwordConfirm", "mismatch", "비밀번호가 서로 달라요.");
        }

        // 2. 입력값에 문제가 있으면 가입 화면을 다시 보여줌
        if (bindingResult.hasErrors()) {
            return "member/join";
        }

        // 3. 가입 시도 (아이디가 중복이면 오류 문구 표시)
        try {
            memberService.join(form);
        } catch (IllegalStateException e) {
            bindingResult.rejectValue("username", "duplicate", e.getMessage());
            return "member/join";
        }

        // 4. 성공하면 로그인 화면으로 이동하면서 안내 문구 전달
        redirectAttributes.addFlashAttribute("message", "회원가입이 완료됐어요! 로그인해 주세요.");
        return "redirect:/login";
    }

    /**
     * 로그인 화면 보여주기 (GET /login)
     * 로그인 "처리"(POST /login)는 Spring Security가 알아서 하니까 여기선 화면만 보여줘요.
     */
    @GetMapping("/login")
    public String loginForm() {
        return "member/login";
    }

    /**
     * 마이페이지 (GET /mypage) - 로그인한 사람만 들어올 수 있어요.
     * @AuthenticationPrincipal → 지금 로그인한 회원의 PrincipalDetails를 자동으로 넣어줘요.
     */
    @GetMapping("/mypage")
    public String mypage(@AuthenticationPrincipal PrincipalDetails principal, Model model) {
        model.addAttribute("member", principal.getMember());
        return "member/mypage";
    }
}