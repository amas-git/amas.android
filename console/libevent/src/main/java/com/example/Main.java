package com.example;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import a.m.a.s.L;

public class Main {

    public static Pattern REGEX_HTML_P = Pattern.compile("<p>(.+?)</p>");
    public static String xxx = "<p><img src='http://img1.store.ksmobile.net/cmnews/20161203/13/57235_a83b2322_148077208723_640_480.jpg' />HBO</p><p>This week, the tech world was hyperventilating over the latest attempt to revolutionize TV with the launch of AT&T's DirecTV Now, a streaming service that delivers live TV over the internet starting at $35 per month.</p><p>On paper, it sounds like a stellar deal. No contracts. No equipment. No waiting for a contractor to come to your house and drill holes in your wall or bolt a satellite dish to your roof. Just the content you want on the device you like to use.</p><p>Maybe DirecTV Now is the start of something bold and new, but after testing it for the last few days, it's clear that it simply isn't as good as traditional pay-TV and not even close to fixing the fundamental problems that have caused so many to cut the cord from cable in the first place.</p><p>It's not just DirecTV Now either. Sling TV and PlayStation Vue are two similar services that have the same promise as DirecTV Now, yet still fail to deliver. They're incomplete, buggy, unreliable, and there are far too many caveats to what you get for your money compared to traditional cable.</p><p>We've been promised the future of TV is just around the corner for years. Instead, the state of internet TV today is a garbled mess.</p><p>Too many caveats</p><p><img src='http://img1.store.ksmobile.net/cmnews/20161203/13/30192_d6d82958_148077208855_640_360.jpg' />HBO</p><p>None of the streaming TV services offer all the channels you want. In DirecTV Now's case for example, that means no CBS, Showtime, and some local news or sports stations. It all depends on where you live, and unlike traditional cable, you just have to hope your DirecTV has what you want in your market.</p><p>There's also no DVR option with DirecTV Now and Sling TV (both say it's on the way though), and on-demand options are limited or hard to track. An example: I could watch episodes 1, 2, 3, 4, and 6 of “Westworld” on-demand through DirecTV Now, but not episode 5 for some reason. Other shows have 72-hour windows for on demand viewing, confusing things even further.</p><p>Then there are restrictions on NFL games on mobile devices. And how many devices can be streaming at the same time. And <a href=\"http://www.businessinsider.com/att-directv-now-net-neutrality-zero-rating-2016-11?utm_source=mobilesrepublic&utm_medium=referral&utm_term=main\">some thorny net neutrality issues</a>.</p><p>I can go on and on, but you probably get the idea. (My colleague Nathan McAlone <a href=\"http://www.businessinsider.com/att-directv-now-fine-print-2016-11?utm_source=mobilesrepublic&utm_medium=referral&utm_term=main\">listed more caveats with DirecTV Now here</a>.) With all these streaming services, you have to compromise on a lot of features we've learned to take for granted just so you can stream TV to your devices and save a little money. But those savings don't add up when you take into account everything you have to give up.</p><p>Bugs</p><p><img src='http://img1.store.ksmobile.net/cmnews/20161203/13/155129_5205234e_148077208954_640_360.jpg' />HBO</p><p>It's been well over a year since Sling TV launched and just a few days since DirecTV Now's debut, but their bugs and glitches are pretty similar.</p><p>DirecTV Now has been off to a particularly bad start. On Thursday, I couldn't stream for more than five minutes without the video freezing. I also got an error message that said I was streaming on too many devices to watch on my Apple TV, even though I knew for a fact that wasn't the case. Several other customers <a href=\"http://www.businessinsider.com/directv-now-problems-bugs-2016-12?utm_source=mobilesrepublic&utm_medium=referral&utm_term=main\">had the same issues</a>, but a company spokesperson told Business Insider the problems are being worked out.</p><p>Plus, most of these live-streaming services are usually behind the actual live feed. When I was watching the Democratic and Republican conventions this summer on Sling TV, CNN's time stamp was always at least a minute or two behind real time. Sling TV has had an especially tough time keeping up with major shows like last year's “Mad Men” finale.</p><p>Despite the flaws with cable, it never stumbles as often as streaming services do. The technology is clearly way too early to stand toe to toe with traditional cable's reliability. I'm sure it'll get better one day, but it's a mess for now.</p><p>The dream</p><p>People who cut the cord do so because they think cable is broken. Providing essentially the same product as before over the internet for a little cheaper isn't revolutionary, it's just polishing the turd that is the pay-TV experience.</p><p>Just because DirecTV and Sling TV are able to beam TV to you over the internet doesn't mean it's any better or fundamentally different. DirecTV Now is being pitched as an alternative for cord cutters, but it's just giving them the same product they rejected in the first place.</p><p>The real dream is a reimagining of the cable TV package — a merging of on-demand and live, linear TV when you want it. Call it the Netflixification of TV.</p><p>That's not where we're at yet. I have no doubt these services will continue to improve, but the transition isn't going to be as simple as it was when music shifted from CDs to digital downloads to all-you-can-eat packages on Spotify or Apple Music.</p><p>It's no wonder Apple abandoned its plans to launch its own streaming TV service last year. Networks simply put too many restrictions on their content that Apple couldn't work with. Instead of releasing an incomplete product that mulled its vision, Apple chose not to release anything for the time being. (It also probably didn't help that Apple VP Eddy Cue showed up to negotiations with TV executives wearing jeans and a Hawaiian T-shirt, <a href=\"http://www.wsj.com/articles/apples-hard-charging-tactics-hurt-tv-expansion-1469721330\">as The Wall Street Journal reported</a>, but that's another issue.)</p><p>We're in the experimental phase</p><p>DirecTV Now, Sling TV, and the rest are just experiments. And as the companies learn what works and what doesn't, they'll get better over time. Some people will sign up for them, sure, but they're hardly viable replacements for traditional paid TV. Even worse, they're not what cord-cutters are asking for.</p><p>Instead, they're a way for companies to gauge interest in over-the-top services and protect themselves against the oncoming generations that'll grow up never experiencing cable in the first place.</p><p>But for now, plenty of people are willing to pay for cable, despite its flaws. The reality is this: If you want the best pay-TV experience, you're going to be stuck with the traditional bundle for several more years.</p><p>NOW WATCH: <a href=\"http://www.businessinsider.com/what-like-use-directvnow-direc-tv-now-directv-att-cable-internet-2016-11?utm_source=mobilesrepublic&utm_medium=referral&utm_term=main>Here's what it's like to use DirecTV Now — the $35 online-only cable service trying to change how we watch TV</a></p><p><b></p>\n";
    /**
     * Extract HTML p label
     * @param body HTML body
     * @param limit Max content length
     * @return text in p labevl
     */
    public static String trimBody(final String body, int limit) {
        StringBuilder sb = new StringBuilder();
        Matcher m  = REGEX_HTML_P.matcher(body);

        while (m.find()) {
            sb.append(normalizeHtmlTag(m.group(1)));
            if(sb.length() >= limit) {
                sb.setLength(limit);
                break;
            }
        }

        return sb.toString();
    }

    public static String normalizeHtmlTag(String html) {
        html = html.trim().replaceAll("<(a|img).+?/>", "").trim();
        html = html.replaceAll("(<a.+?>|</a>)", "").trim();
        return html;
    }


    public static void main(String[] argv) {
        System.out.println(""+trimBody(xxx,200));
        System.out.println((Integer)(128) == (Integer)(128));
        System.out.println((Integer)(127) == (Integer)(127));
    }
}
