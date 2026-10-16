# Attribution

This is a derivative of [Atomberg-BLDC-IR-Remote](https://github.com/Mobasheera/Atomberg-BLDC-IR-Remote)
by Mobashshir Ahsan (Mobasheera). The original MIT copyright and license remain
unchanged in [LICENSE](LICENSE). A copy is bundled in the app's About screen.

This version adds a compact native Android interface, hardware capability checks,
transmission error handling, waveform regression tests, and corrected LED control.

The LED correction uses Pramod Ghuge's February 22, 2025 capture comment on
[Glen Zac's IR research](https://glenzac.wordpress.com/2020/03/25/atomberg-gorilla-efficio-bldc-fan-ir-remote-codes/).
Converting the published MSB-first `0x00CFE916` to this app's LSB-first format gives
`0x6897F300`. It fixes a bit-order error in the imported profile; this project does
not claim to have discovered the Atomberg protocol or that LED capture.

Normal controls and corrected LED control were confirmed by the user on an
Atomberg Renesa 1200 mm with a Realme GT 7T. This does not certify other models.

This is an independent community application, not an official Atomberg product.
